package com.hls.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hls.content.po.Comment;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 评论表（歌曲/歌单评论） Mapper 接口
 * </p>
 *
 * @author hls
 * @since 2026-01-17
 */
@Mapper
public interface CommentMapper extends BaseMapper<Comment> {

}
