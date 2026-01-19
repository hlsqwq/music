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
 * 歌单信息表
 * </p>
 *
 * @author hls
 * @since 2026-01-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("song_list")
public class SongList implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 歌单ID（主键）
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 歌单名称
     */
    private String name;

    /**
     * 创建用户ID
     */
    private Integer userId;

    /**
     * 歌单封面URL
     */
    private String avatar;

    /**
     * 播放数
     */
    private Integer playNum;

    /**
     * 点赞数
     */
    private Integer likeNum;

    /**
     * 歌单简介
     */
    private String introduction;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;


}
