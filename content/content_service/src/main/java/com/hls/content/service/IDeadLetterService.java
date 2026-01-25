package com.hls.content.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.hls.base.MusicCd;
import com.hls.content.po.DeadLetter;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author hls
 * @since 2026-01-25
 */
public interface IDeadLetterService extends IService<DeadLetter> {

    /**
     * @param cd
     * @param cause
     */
    void saveMessageToDB(MusicCd cd, String cause);
}
