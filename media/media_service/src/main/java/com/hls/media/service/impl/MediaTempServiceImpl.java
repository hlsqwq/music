package com.hls.media.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hls.media.config.XxlJobConfig;
import com.hls.media.po.MediaTemp;
import com.hls.media.mapper.MediaTempMapper;
import com.hls.media.service.IMediaTempService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author hls
 * @since 2026-01-21
 */
@RequiredArgsConstructor
@Service
public class MediaTempServiceImpl extends ServiceImpl<MediaTempMapper, MediaTemp> implements IMediaTempService {

    private final XxlJobConfig xxlJobConfig;



    @Override
    public List<MediaTemp> getTask(){
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime time = now.minusMinutes(xxlJobConfig.clearTime);
        LambdaQueryWrapper<MediaTemp> qw = new LambdaQueryWrapper<MediaTemp>()
                .lt(MediaTemp::getCreateTime, time)
                .lt(MediaTemp::getRetry,3);
        return list(qw);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void addTryNum(Long id) {
        MediaTemp byId = getById(id);
        byId.setRetry(byId.getRetry()+1);
        if(byId.getRetry()>3){
            removeById(id);
            return;
        }
        updateById(byId);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delTask(Long id) {
        removeById(id);
    }


}
