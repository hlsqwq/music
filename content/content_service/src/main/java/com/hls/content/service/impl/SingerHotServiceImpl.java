package com.hls.content.service.impl;


import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hls.content.mapper.SingerHotMapper;
import com.hls.content.po.SingerHot;
import com.hls.content.service.ISingerHotService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 歌手热度表 服务实现类
 * </p>
 *
 * @author hls
 * @since 2026-01-24
 */
@Service
public class SingerHotServiceImpl extends ServiceImpl<SingerHotMapper, SingerHot> implements ISingerHotService {




    @Transactional(rollbackFor = Exception.class)
    @Override
    public void refreshHot(Integer singerId, long likeNum, long playNum) {
        LambdaUpdateWrapper<SingerHot> eq = new LambdaUpdateWrapper<SingerHot>()
                .eq(SingerHot::getId, singerId);
        eq.set(SingerHot::getLikeNum, likeNum);
        eq.set(SingerHot::getPlayNum, playNum);
        eq.set(SingerHot::getHot,(long)likeNum*0.5+playNum*0.5);
        update(eq);
    }



}
