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

    void add_singer(Long userId, SingerDto singerDto);

    void update_singer(Long userId, EditSingerDto editSingerDto);

    List<EditSingerDto> getTop10(int id);

}
