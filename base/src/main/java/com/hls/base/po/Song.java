package com.hls.base.po;

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
 * 歌曲信息表
 * </p>
 *
 * @author hls
 * @since 2026-03-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("song")
public class Song implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 歌曲ID（主键）
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 歌手ID
     */
    private Integer singerId;

    /**
     * 歌手名
     */
    private String singerName;

    /**
     * 歌词（长文本id）
     */
    private Integer lyricId;

    /**
     * 音源id
     */
    private Integer musicId;

    /**
     * 音源
     */
    private String musicUrl;

    /**
     * 歌曲名称
     */
    private String name;

    /**
     * 媒体id
     */
    private Integer avatarId;

    /**
     * 媒体url
     */
    private String avatarUrl;

    /**
     * 专辑id
     */
    private Integer albumId;

    /**
     * 专辑名称
     */
    private String albumName;

    /**
     * 专辑中的序号
     */
    private Integer albumOrder;

    /**
     * 持续时间（秒）
     */
    private Integer duration;

    /**
     * 收藏数
     */
    private Long favoriteNum;

    /**
     * 播放量
     */
    private Long playNum;

    /**
     * 评论数
     */
    private Long commentNum;

    /**
     * 热度
     */
    private Long hot;

    /**
     * 长文本id
     */
    private Integer introductionId;

    /**
     * 歌曲简介
     */
    private String introduction;

    /**
     * 歌曲审核状态
     */
    private String status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;


}
