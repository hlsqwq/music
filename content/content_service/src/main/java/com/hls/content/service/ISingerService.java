package com.hls.content.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hls.base.R;
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

    /**
     * 获取top10歌手
     *
     * @param id 分类id
     * @return list<singer>
     */
    R<Object> getTop10(int id);

    void del_singer(Integer id);
}
