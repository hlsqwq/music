package com.hls.content.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.hls.content.po.SingerHot;

/**
 * <p>
 * 歌手热度表 服务类
 * </p>
 *
 * @author hls
 * @since 2026-01-24
 */
public interface ISingerHotService extends IService<SingerHot> {


    void refreshHot(Integer singerId, long likeNum, long playNum);
}
