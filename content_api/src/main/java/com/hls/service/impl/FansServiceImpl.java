package com.hls.service.impl;

import com.hls.po.Fans;
import com.hls.mapper.FansMapper;
import com.hls.service.IFansService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户粉丝表 服务实现类
 * </p>
 *
 * @author hls
 * @since 2026-01-17
 */
@Service
public class FansServiceImpl extends ServiceImpl<FansMapper, Fans> implements IFansService {

}
