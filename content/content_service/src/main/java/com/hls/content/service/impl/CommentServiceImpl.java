package com.hls.content.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hls.base.PageParam;
import com.hls.base.PageResult;
import com.hls.base.R;
import com.hls.base.config.MqConfig;
import com.hls.base.exception.MusicException;
import com.hls.base.po.User;
import com.hls.base.utils.MqBase;
import com.hls.base.utils.RedisBase;
import com.hls.base.utils.RedisKeys;
import com.hls.content.dto.CommentMessage;
import com.hls.content.feign.UserClient;
import com.hls.content.mapper.CommentMapper;
import com.hls.content.po.Comment;
import com.hls.content.po.TextInfo;
import com.hls.content.service.ICommentService;
import com.hls.content.service.ITextInfoService;
import com.hls.content.vo.CommentVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 评论表（歌曲/歌单评论） 服务实现类
 * </p>
 *
 * @author hls
 * @since 2026-01-17
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements ICommentService {

    private static final int HOT_MAX_SIZE = 500;
    private final RedisBase redisBase;
    private final RedisKeys redisKeys;
    private final UserClient userClient;
    private final MqBase mqBase;
    private final RedissonClient redissonClient;
    private final ITextInfoService textInfoService;
    private final TextInfo textInfo;

    /**
     * 获取热评列表
     *
     * @param type      id 类型  mv，song
     * @param id        ID
     * @param pageParam page
     * @return 热评列表
     */
    @Override
    public PageResult<CommentVo> listHotComment(RedisKeys.TableType type, Integer id, PageParam pageParam) {
        String hotKey = redisKeys.commentList(type, id);
        String flag = redisKeys.commentFlag(type, id, pageParam);

        // 1. 查 Redis 热评
        Set<Object> topRange = redisBase.getTopRange(hotKey,
                getStart(pageParam.getNum(), pageParam.getSize()),
                getEnd(pageParam.getNum(), pageParam.getSize()));

        if (!CollectionUtils.isEmpty(topRange)) {
            // 缓存命中 → 正常返回
            redisBase.expire(hotKey);
            List<Integer> list = topRange.stream().map(String::valueOf).map(Integer::parseInt).toList();
            List<CommentVo> commentDtos = buildCommentListByIds(list);
            String s = redisKeys.commentNumber(type, id);
            Long l = redisBase.get(s, Long.class);
            PageResult<CommentVo> result = new PageResult<>();
            result.setTotal(l);
            result.setItem(commentDtos);
            result.setNum(pageParam.getNum());
            result.setSize(pageParam.getSize());
            return result;
        }

        PageResult<CommentVo> result = new PageResult<>();
        result.setNum(pageParam.getNum());
        result.setSize(pageParam.getSize());

        //防止缓存穿透 redis
        if (redisBase.get(flag, String.class) != null) {
            return result;
        }

        RLock lock = redissonClient.getLock(hotKey);
        if (lock.tryLock()) {
            try {
                Page<Comment> objectPage = Page.of(pageParam.getNum(), pageParam.getSize());
                LambdaQueryWrapper<Comment> qw = new LambdaQueryWrapper<Comment>()
                        .eq(Comment::getRootId, 0)
                        .orderByDesc(Comment::getHot);
                Page<Comment> page = page(objectPage, qw);

                if (page.getRecords().isEmpty()) {
                    redisBase.set(flag, "empty", 5L, TimeUnit.MINUTES);
                    return result;
                }
                redisBase.del(flag);
                result.setTotal(page.getTotal());
                List<CommentVo> commentDtos = buildCommentList(page.getRecords());
                result.setItem(commentDtos);
                if (redisBase.zSetSize(hotKey) < HOT_MAX_SIZE) {
                    mqBase.sendMessageToMusic(MqConfig.COMMENT_KEY, new CommentMessage(id, type, commentDtos));
                }
                return result;
            } catch (Exception e) {
                e.printStackTrace();
                return result;
            } finally {
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
        } else {
            return result;
        }
    }

    private List<CommentVo> buildCommentList(List<Comment> comments) {
        List<Integer> userIds = comments.stream().map(Comment::getUserId).toList();
        List<User> users = userClient.list(userIds);
        ArrayList<CommentVo> ans = new ArrayList<>();
        for (int i = 0; i < comments.size(); i++) {
            CommentVo commentDto = BeanUtil.copyProperties(comments.get(i), CommentVo.class);
            commentDto.setAvatar(users.get(i).getAvatarUrl());
            ans.add(commentDto);
        }
        return ans;
    }


    private List<CommentVo> buildCommentListByIds(List<Integer> list) {
        List<Comment> comments = listByIds(list);
        return buildCommentList(comments);
    }


    private int getStart(Integer pageNum, Integer pageSize) {
        return (pageNum - 1) * pageSize;
    }

    private int getEnd(Integer pageNum, Integer pageSize) {
        return pageNum * pageSize - 1;
    }

    /**
     * 发布评论
     *
     * @param comment 评论信息
     * @param type    评论对象类型
     * @return 是否成功
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean publishComment(Comment comment, RedisKeys.TableType type) {
        try {
            // 1. 处理长文本
            if (comment.getContent() != null && comment.getContent().length() > 50) {
                // 截取前50字符存入Comment表
                String shortContent = comment.getContent().substring(0, 50);

                // 完整内容存入TextInfo表
                TextInfo textInfo = new TextInfo();
                textInfo.setContent(comment.getContent());
                comment.setContent(shortContent);
                textInfoService.save(textInfo);
                comment.setLongTextId(textInfo.getId());
            }

            // 2. 保存评论到数据库
            boolean saved = save(comment);
            if (!saved) {
                log.error("发布评论失败，保存到数据库失败");
                return false;
            }

            // 3. 调用Redis原子递增评论总数
            String commentNumKey = redisKeys.commentNumber(type, comment.getTypeId());
            redisBase.increment(commentNumKey);

            // 4. 校验Redis ZSet大小，若<500，则发送MQ消息同步该评论到ZSet
            String hotKey = redisKeys.commentList(type, comment.getTypeId());
            long size = redisBase.zSetSize(hotKey);
            if (size < HOT_MAX_SIZE) {
                // 计算初始热度（初始点赞数为0）
                double initialHotScore = 0;
                // 发送MQ消息同步到ZSet
                mqBase.sendMessageToMusic(MqConfig.COMMENT_KEY,
                        new CommentMessage(comment.getTypeId(), type,
                                buildCommentList(List.of(comment))));
            }

            return true;
        } catch (Exception e) {
            log.error("发布评论失败", e);
            throw e;
        }
    }

    /**
     * 删除评论
     *
     * @param commentId 评论ID
     * @param type      评论对象类型
     * @param id        评论对象ID
     * @return 是否成功
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteComment(Integer commentId, RedisKeys.TableType type, Integer id) {
        try {
            // 1. 获取评论信息
            Comment comment = getById(commentId);
            if (comment == null) {
                log.error("删除评论失败，评论不存在，commentId: {}", commentId);
                return false;
            }

            // 2. 删除TextInfo表对应记录（如果存在）
            if (comment.getLongTextId() != null) {
                textInfoService.removeById(comment.getLongTextId());
            }

            // 3. 删除Comment表对应记录
            boolean deleted = removeById(commentId);
            if (!deleted) {
                log.error("删除评论失败，从数据库删除失败，commentId: {}", commentId);
                return false;
            }

            // 4. 同步删除Redis ZSet中的该成员
            String hotKey = redisKeys.commentList(type, id);
            redisBase.zSetDelete(hotKey, commentId);

            // 5. 评论总数递减
            String commentNumKey = redisKeys.commentNumber(type, id);
            redisBase.decrement(commentNumKey);

            return true;
        } catch (Exception e) {
            log.error("删除评论失败", e);
            throw e;
        }
    }

    /**
     * 评论点赞
     *
     * @param commentId 评论ID
     * @param type      评论对象类型
     * @param id        评论对象ID
     * @return 最新点赞数
     */
    @Override
    public Long likeComment(Integer commentId, RedisKeys.TableType type, Integer id) {
        try {
            String likeKey = redisKeys.commentLike(type, commentId);
            String hotKey = redisKeys.commentList(type, id);

            // 1. 点赞数原子增加
            Long newLikes = redisBase.increment(likeKey);

            // 2. 计算新热度值
            double newHotScore = newLikes;

            // 3. 更新 ZSet (如果 ID 已存在则更新分数，不存在则添加)
            redisBase.zSetAdd(hotKey, commentId, newHotScore);

            // 4. 关键：修剪 ZSet 长度，只保留前 500 名
            // 删除排名 0 到 (size - 501) 的元素，剩下的就是 Top 500
            redisBase.zSetDeleteTail(hotKey, HOT_MAX_SIZE);
            return newLikes;
        } catch (Exception e) {
            log.error("评论点赞失败", e);
            throw e;
        }
    }


    /**
     * 获取子评论/回复列表
     *
     * @param rootId    一级评论 ID
     * @param parentId  回复 id
     * @param pageParam 分页参数
     * @return 子评论/回复列表
     */
    @Override
    public R<PageResult<CommentVo>> listSubComment(Integer rootId, Integer parentId, PageParam pageParam) {
        try {
            // 1. 从数据库分页查询子评论
            Page<Comment> page = Page.of(pageParam.getNum(), pageParam.getSize());
            LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<Comment>()
                    .eq(Comment::getRootId, rootId)
                    .ne(Comment::getRootId, 0) // 排除一级评论
                    .eq(Comment::getParentId, parentId)
                    .orderByDesc(Comment::getHot);
            Page<Comment> commentPage = page(page, queryWrapper);

            List<Comment> comments = commentPage.getRecords();
            PageResult<CommentVo> result = new PageResult<>();
            result.setTotal(commentPage.getTotal());
            result.setNum(pageParam.getNum());
            result.setSize(pageParam.getSize());

            if (CollectionUtils.isEmpty(comments)) {
                result.setItem(new ArrayList<>());
                return R.success(result);
            }

            // 3. 填充用户信息
            List<CommentVo> commentVos = buildCommentList(comments);

            result.setItem(commentVos);
            return R.success(result);
        } catch (Exception e) {
            log.error("获取子评论列表失败", e);
            throw new MusicException("获取子评论列表失败");
        }
    }

    @Override
    public R<Comment> getDetail(Integer commentId) {
        Comment byId = getById(commentId);
        if (byId.getLongTextId() == null) {
            return R.failure(null);
        }
        TextInfo byId1 = textInfoService.getById(byId.getLongTextId());
        byId.setContent(byId1.getContent());
        return R.success(byId);
    }

    /**
     * 处理长文本还原
     *
     * @param comments 评论列表
     * @return 处理后的评论列表
     */
    private List<Comment> processLongText(List<Comment> comments) {
        // 收集所有需要还原长文本的评论ID
        List<Integer> longTextIds = comments.stream()
                .filter(comment -> comment.getLongTextId() != null)
                .map(Comment::getLongTextId)
                .toList();

        if (CollectionUtils.isEmpty(longTextIds)) {
            return comments;
        }

        // 批量查询长文本
        List<TextInfo> textInfos = textInfoService.listByIds(longTextIds);
        if (CollectionUtils.isEmpty(textInfos)) {
            return comments;
        }

        // 构建长文本ID到内容的映射
        java.util.Map<Integer, String> textInfoMap = textInfos.stream()
                .collect(java.util.stream.Collectors.toMap(TextInfo::getId, TextInfo::getContent));

        // 还原长文本
        for (Comment comment : comments) {
            if (comment.getLongTextId() != null && textInfoMap.containsKey(comment.getLongTextId())) {
                comment.setContent(textInfoMap.get(comment.getLongTextId()));
            }
        }

        return comments;
    }

}
