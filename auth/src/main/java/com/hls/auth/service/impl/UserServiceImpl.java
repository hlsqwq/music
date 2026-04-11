package com.hls.auth.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hls.auth.mapper.UserMapper;
import com.hls.auth.service.IUserService;
import com.hls.base.R;
import com.hls.base.po.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 用户信息表 服务实现类
 * </p>
 *
 * @author hls
 * @since 2026-01-17
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    private final PasswordEncoder passwordEncoder;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public R<Object> register(User user) {
        LambdaQueryWrapper<User> eq = new LambdaQueryWrapper<User>()
                .eq(User::getAccount, user.getAccount());
        User one = getOne(eq);
        if (one != null) {
            return R.failure("账号已注册");
        }
        user.setPasswd(passwordEncoder.encode(user.getPasswd()));
        save(user);
        return R.success();
    }
}
