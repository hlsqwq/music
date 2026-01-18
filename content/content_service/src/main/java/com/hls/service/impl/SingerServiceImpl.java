package com.hls.service.impl;


import com.hls.R;
import com.hls.mapper.SingerMapper;
import com.hls.po.Singer;
import com.hls.service.ISingerService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 歌手信息表 服务实现类
 * </p>
 *
 * @author hls
 * @since 2026-01-17
 */
@Service
public class SingerServiceImpl extends ServiceImpl<SingerMapper, Singer> implements ISingerService {



    @Override
    public R create(Long userId, Singer singer) {
        //todo 权限判断

        return null;
    }
}
