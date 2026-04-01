package com.hls.content.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hls.base.R;
import com.hls.content.dto.EditSingerDto;
import com.hls.content.dto.SingerDto;
import com.hls.content.po.Singer;

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

    /**
     * 获取歌手的粉丝数
     * @param singerId 歌手id
     * @return long
     */
    R<Object> getFans(Integer singerId);

    /**
     * 增加歌手的粉丝
     *
     * @param singerId 歌手id
     * @return 更新后的粉丝数 long
     */
    R<Object> follow(Integer singerId);
}
