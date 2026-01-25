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
 * 专辑信息表
 * </p>
 *
 * @author hls
 * @since 2026-01-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("album")
public class Album implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 专辑ID（主键）
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 歌手ID
     */
    private Long singerId;

    /**
     * 上传用户ID
     */
    private Integer userId;


    /**
     * 专辑名
     */
    private String name;

    /**
     * 专辑封面URL
     */
    private String avatar;

    /**
     * 专辑简介
     */
    private String introduction;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;


}
