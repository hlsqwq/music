package com.hls.content.po;

import cn.hutool.core.date.DateTime;
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
 * @since 2026-01-17
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
     * 上传用户ID
     */
    private Integer userId;

    /**
     * 关联媒体文件ID
     */
    private Integer mediaId;

    /**
     * 歌曲名称
     */
    private String name;

    /**
     * 专辑名
     */
    private String album;

    /**
     * 持续时长
     */
    private DateTime duration;


    /**
     * 点赞数
     */
    private Integer likeNum;

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
