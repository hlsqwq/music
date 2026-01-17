package com.hls.service.impl;


import com.hls.mapper.CommentMapper;
import com.hls.po.Comment;
import com.hls.service.ICommentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
