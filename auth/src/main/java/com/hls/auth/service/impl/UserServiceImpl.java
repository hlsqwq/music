package com.hls.auth.service.impl;



import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.hls.auth.mapper.UserMapper;
import com.hls.auth.po.User;
import com.hls.auth.service.IUserService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户信息表 服务实现类
 * </p>
 *
 * @author hls
 * @since 2026-01-17
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

}
