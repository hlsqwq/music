package com.hls.content.dto;


import com.hls.base.utils.RedisKeys;
import lombok.Data;


@Data
public class HotDataDto {


    /**
     * 实体ID
     */
    private Integer id;


    /**
     * 类型：song, mv,album,singer
     */
    private RedisKeys.TableType type;


    /**
     * 播放量/新增
     */
    private Long playNum;

    /**
     * 点赞数/新增
     */
    private Long likeNum;

    private Long fansNum;
    private Long favoriteNum;
    private Long commentNum;

}