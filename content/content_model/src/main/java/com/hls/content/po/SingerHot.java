package com.hls.content.po;

import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 歌手热度表
 * </p>
 *
 * @author hls
 * @since 2026-01-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("singer_hot")
public class SingerHot implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 歌手id
     */
    private Integer id;

    /**
     * 点赞数
     */
    private Long likeNum;

    /**
     * 播放量
     */
    private Long playNum;

    /**
     * 热度
     */
    private Long hot;


}
