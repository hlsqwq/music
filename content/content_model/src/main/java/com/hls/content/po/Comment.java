package com.hls.content.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 评论表（歌曲/歌单评论）
 * </p>
 *
 * @author hls
 * @since 2026-03-28
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
     * 对象类型
     */
    private String objType;

    /**
     * 关联ID
     */
    private Integer typeId;

    /**
     * 评论用户ID
     */
    private Integer userId;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 长文本id
     */
    private Integer longTextId;

    /**
     * 一级评论id
     */
    private Integer rootId;

    /**
     * 回复id
     */
    private Integer parentId;

    /**
     * 评论点赞数
     */
    private Long likeNum;

    /**
     * 热度
     */
    private Long hot;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;


}
