package com.hls.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hls.R;
import com.hls.po.Singer;

/**
 * <p>
 * 歌手信息表 服务类
 * </p>
 *
 * @author hls
 * @since 2026-01-17
 */
public interface ISingerService extends IService<Singer> {

    R create(Long userId, Singer singer);
}
