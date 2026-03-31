package com.hls.content.controller;

import com.hls.base.PageParam;
import com.hls.base.PageResult;
import com.hls.base.R;
import com.hls.base.utils.RedisKeys;
import com.hls.content.po.Comment;
import com.hls.content.service.ICommentService;
import com.hls.content.vo.CommentVo;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 评论表（歌曲/歌单评论） 前端控制器
 * </p>
 *
 * @author hls
 * @since 2026-01-17
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/comment")
public class CommentController {

    private final ICommentService commentService;

    /**
     * 获取热评列表
     *
     * @param type     评论对象类型 (mv, song)
     * @param id       评论对象 ID
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @return 热评列表
     */
    @GetMapping("/hot")
    public PageResult<CommentVo> listHotComment(@RequestParam String type, @RequestParam Integer id,
                                                @RequestParam Integer pageNum, @RequestParam Integer pageSize) {
        RedisKeys.TableType tableType = RedisKeys.TableType.valueOf(type);
        PageParam pageParam = new PageParam();
        pageParam.setNum(pageNum);
        pageParam.setSize(pageSize);
        return commentService.listHotComment(tableType, id, pageParam);
    }

    /**
     * 发布评论
     *
     * @param comment 评论信息
     * @param type    评论对象类型 (mv, song)
     * @return 是否成功
     */
    @PostMapping("/publish")
    public void publishComment(@RequestBody Comment comment, @RequestParam String type) {
        RedisKeys.TableType tableType = RedisKeys.TableType.valueOf(type);
        commentService.publishComment(comment, tableType);
    }

    /**
     * 删除评论
     *
     * @param commentId 评论 ID
     * @param type      评论对象类型 (mv, song)
     * @param id        评论对象ID
     * @return 是否成功
     */
    @DeleteMapping("/delete")
    public void deleteComment(@RequestParam Integer commentId, @RequestParam String type,
                              @RequestParam Integer id) {
        RedisKeys.TableType tableType = RedisKeys.TableType.valueOf(type);
        commentService.deleteComment(commentId, tableType, id);
    }

    /**
     * 评论点赞
     *
     * @param commentId 评论 ID
     * @param type      评论对象类型 (mv, song)
     * @param id        评论对象 ID
     * @return 最新点赞数
     */
    @PostMapping("/like")
    public Long likeComment(@RequestParam Integer commentId, @RequestParam String type,
                            @RequestParam Integer id) {
        RedisKeys.TableType tableType = RedisKeys.TableType.valueOf(type);
        return commentService.likeComment(commentId, tableType, id);
    }


    /**
     * 获取子评论/回复列表
     *
     * @param rootId    一级评论 ID
     * @param parentId  回复 id
     * @param pageParam 分页参数
     * @return 子评论/回复列表
     */
    @GetMapping("/sub/{rootId}/{parentId}")
    public R<PageResult<CommentVo>> listSubComment(@PathVariable Integer rootId,
                                                   @PathVariable Integer parentId,
                                                   @RequestParam PageParam pageParam) {
        return commentService.listSubComment(rootId, parentId, pageParam);
    }


    /**
     * 获取评论详情
     *
     * @param commentId 评论 id
     * @return 评论详情
     */
    @GetMapping("/detail/{commentId}")
    public R<Comment> getDetail(@PathVariable Integer commentId) {
        return commentService.getDetail(commentId);
    }

}
