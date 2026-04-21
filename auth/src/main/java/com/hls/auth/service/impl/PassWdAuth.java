package com.hls.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hls.auth.feign.SmsClient;
import com.hls.auth.po.AuthParams;
import com.hls.base.exception.MusicException;
import com.hls.base.po.User;
import com.hls.auth.service.Auth;
import com.hls.auth.service.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Slf4j
@RequiredArgsConstructor
@Service(value = "passwd")
public class PassWdAuth implements Auth {

    private final IUserService userService;
    private final PasswordEncoder passwordEncoder;
    private final SmsClient  smsClient;

    @Override
    public User auth(AuthParams authParams) {
        LambdaQueryWrapper<User> eq = new LambdaQueryWrapper<User>()
                .eq(User::getAccount, authParams.getAccount());
        User one = userService.getOne(eq);
        if(one==null){
            MusicException.cast("没有这用户");
        }
        //框架校验密码 省略
//        if (!smsClient.captchaValidate(authParams.getCheckCodeKey(), authParams.getCheckCode())) {
//            MusicException.cast("验证码错误");
//        }
        return one;
    }
}
