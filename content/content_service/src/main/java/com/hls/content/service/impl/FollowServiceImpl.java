package com.hls.content.service.impl;



import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hls.canal.service.IFollowService;
import com.hls.content.mapper.FollowMapper;
import com.hls.content.po.Follow;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户关注表 服务实现类
 * </p>
 *
 * @author hls
 * @since 2026-01-17
 */
@Service
public class FollowServiceImpl extends ServiceImpl<FollowMapper, Follow> implements IFollowService {

}
