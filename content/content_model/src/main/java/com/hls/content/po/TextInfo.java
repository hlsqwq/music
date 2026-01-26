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
 * 文本信息表（歌词/歌手/专辑简介）
 * </p>
 *
 * @author hls
 * @since 2026-01-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("text_info")
public class TextInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 文本ID（主键）
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 关联歌曲ID（歌词）
     */
    private Long songId;

    /**
     * 关联歌手ID（歌手简介）
     */
    private Long singerId;

    /**
     * 关联专辑ID（专辑简介）
     */
    private Long albumId;

    /**
     * 创建用户ID
     */
    private Long userId;

    /**
     * 文本内容（歌词/简介）
     */
    private String content;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;


}
