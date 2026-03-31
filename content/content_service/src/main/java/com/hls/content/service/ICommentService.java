package com.hls.content.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hls.base.PageParam;
import com.hls.base.PageResult;
import com.hls.base.R;
import com.hls.base.utils.RedisKeys;
import com.hls.content.vo.CommentVo;
import com.hls.content.po.Comment;

/**
 * <p>
 * 评论表（歌曲/歌单评论） 服务类
 * </p>
 *
 * @author hls
 * @since 2026-01-17
 */
public interface ICommentService extends IService<Comment> {

    /**
     * 获取热评列表
     * 
     * @param type      id 类型 mv，song
     * @param id        ID
     * @param pageParam page
     * @return 热评列表
     */
    PageResult<CommentVo> listHotComment(RedisKeys.TableType type, Integer id, PageParam pageParam);

    /**
     * 发布评论
     * 
     * @param comment 评论信息
     * @param type    评论对象类型
     * @return 是否成功
     */
    boolean publishComment(Comment comment, RedisKeys.TableType type);

    /**
     * 删除评论
     * 
     * @param commentId 评论ID
     * @param type      评论对象类型
     * @param id        评论对象ID
     * @return 是否成功
     */
    boolean deleteComment(Integer commentId, RedisKeys.TableType type, Integer id);

    /**
     * 评论点赞
     * 
     * @param commentId 评论ID
     * @param type      评论对象类型
     * @param id        评论对象ID
     * @return 最新点赞数
     */
    Long likeComment(Integer commentId, RedisKeys.TableType type, Integer id);


    /**
     * 获取子评论/回复列表
     *
     * @param rootId    一级评论 ID
     * @param parentId  回复 id
     * @param pageParam 分页参数
     * @return 子评论/回复列表
     */
    R<PageResult<CommentVo>> listSubComment(Integer rootId, Integer parentId, PageParam pageParam);

    R<Comment> getDetail(Integer commentId);
}
