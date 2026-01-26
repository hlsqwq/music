package com.hls.media.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hls.media.po.MediaTemp;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author hls
 * @since 2026-01-21
 */
public interface IMediaTempService extends IService<MediaTemp> {


    List<MediaTemp> getTask();

    void addTryNum(Long id);

    void delTask(Long id);
}
