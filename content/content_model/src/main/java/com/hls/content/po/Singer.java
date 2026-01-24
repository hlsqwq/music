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
 * 歌手信息表
 * </p>
 *
 * @author hls
 * @since 2026-01-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("singer")
public class Singer implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 歌手ID（主键）
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 歌手姓名
     */
    private String name;

    /**
     * 歌手头像媒体ID
     */
    private String avatar;

    /**
     * 发布歌曲数量
     */
    private Long songNum;

    /**
     * 发布专辑数量
     */
    private Long albumNum;

    /**
     * 发布MV数量
     */
    private Long mvNum;

    /**
     * 歌手简介
     */
    private String introduction;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;


}
