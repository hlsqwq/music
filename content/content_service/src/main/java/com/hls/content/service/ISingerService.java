package com.hls.content.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hls.content.dto.EditSingerDto;
import com.hls.content.dto.SingerDetailDto;
import com.hls.content.dto.SingerDto;
import com.hls.content.po.Singer;

import java.util.List;

/**
 * <p>
 * 歌手信息表 服务类
 * </p>
 *
 * @author hls
 * @since 2026-01-17
 */
public interface ISingerService extends IService<Singer> {

    void add_singer(SingerDto singerDto);

    void update_singer(EditSingerDto editSingerDto);

    List<EditSingerDto> getTop10(int id);

    void del_singer(Integer id);

    /**
     * 增加歌手播放量
     * 
     * @param singerId 歌手ID
     * @return 增加后的播放量
     */
    Long incrPlayNum(Integer singerId);

    /**
     * 增加歌手点赞数
     * 
     * @param singerId 歌手ID
     * @return 增加后的点赞数
     */
    Long incrLikeNum(Integer singerId);
}
