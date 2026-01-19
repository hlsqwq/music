package com.hls.content.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.io.Serializable;


/**
 * <p>
 * 评论表（歌曲/歌单评论）
 * </p>
 *
 * @author hls
 * @since 2026-01-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("comment")
public class Comment implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 评论ID（主键）
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 关联歌曲ID
     */
    private Integer songId;

    /**
     * 关联歌单ID
     */
    private Integer songListId;

    /**
     * 评论用户ID
     */
    private Integer userId;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 父评论ID（0为一级评论）
     */
    private Integer parentId;

    /**
     * 评论点赞数
     */
    private Integer likeNum;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;


}
