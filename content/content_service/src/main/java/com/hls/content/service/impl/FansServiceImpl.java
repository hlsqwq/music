package com.hls.content.service.impl;



import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hls.content.mapper.FansMapper;
import com.hls.content.po.Fans;
import com.hls.content.service.IFansService;
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
