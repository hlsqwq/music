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
 * 歌手mv
 * </p>
 *
 * @author hls
 * @since 2026-03-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("mv")
public class Mv implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * mv名称
     */
    private String name;

    /**
     * 媒资id
     */
    private Integer videoId;

    /**
     * 视频源
     */
    private String video;

    /**
     * 歌曲id
     */
    private Integer songId;

    /**
     * 歌手id
     */
    private Integer singerId;

    /**
     * 歌手名称
     */
    private String singerName;

    /**
     * 封面id
     */
    private Integer avatarId;

    /**
     * 封面
     */
    private String avatarUrl;

    /**
     * 分类id
     */
    private Integer categoryId;

    /**
     * 审核状态
     */
    private String status;

    /**
     * 播放量
     */
    private Long playNum;

    /**
     * 点赞数
     */
    private Long likeNum;

    /**
     * 评论数
     */
    private Long commentNum;

    /**
     * 收藏数
     */
    private Long favoriteNum;

    /**
     * 热度
     */
    private Long hot;

    private LocalDateTime createTime;


}
