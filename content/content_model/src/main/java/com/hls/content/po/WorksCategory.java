package com.hls.content.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 作品分类表
 * </p>
 *
 * @author hls
 * @since 2026-01-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("works_category")
public class WorksCategory implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID（主键）
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 歌曲id
     */
    private Integer songId;

    /**
     * 歌手id
     */
    private Integer singerId;

    /**
     * 标签1
     */
    private Integer flag1;

    /**
     * 标签2
     */
    private Integer flag2;

    /**
     * 标签3
     */
    private Integer flag3;

    /**
     * 热度信息
     */
    private Long hot;


}
