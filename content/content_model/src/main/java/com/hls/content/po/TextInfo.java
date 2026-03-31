package com.hls.content.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableId;
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
 * @since 2026-03-29
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
    private Integer id;

    /**
     * 文本内容
     */
    private String content;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;


}
