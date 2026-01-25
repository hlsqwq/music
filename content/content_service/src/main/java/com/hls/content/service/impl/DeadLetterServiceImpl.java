package com.hls.content.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hls.base.MusicCd;
import com.hls.content.mapper.DeadLetterMapper;
import com.hls.content.po.DeadLetter;
import com.hls.content.service.IDeadLetterService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author hls
 * @since 2026-01-25
 */
@Service
public class DeadLetterServiceImpl extends ServiceImpl<DeadLetterMapper, DeadLetter> implements IDeadLetterService {





    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveMessageToDB(MusicCd cd, String cause) {
        DeadLetter deadLetter = new DeadLetter();
        deadLetter.setMessage(cd.getMessage().toString());
        deadLetter.setCause(cause);
        deadLetter.setRetryCount(cd.getRetryCount());
        deadLetter.setQueue(cd.getRoutingKey());
        deadLetter.setCreateTime(LocalDateTime.now());
        save(deadLetter);
    }

}
