package com.hls.content.service.impl;



import com.hls.content.mapper.CommentMapper;
import com.hls.content.po.Comment;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hls.content.service.ICommentService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 评论表（歌曲/歌单评论） 服务实现类
 * </p>
 *
 * @author hls
 * @since 2026-01-17
 */
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements ICommentService {

}
