package com.hls.auth.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.hls.auth.po.AuthParams;
import com.hls.auth.service.Auth;
import com.hls.auth.service.IUserService;
import com.hls.base.exception.MusicException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserDetailsServiceImpl implements UserDetailsService {


    private final IUserService userService;
    private final ApplicationContext applicationContext;



    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        if(s==null){
            MusicException.cast("信息不可为空");
        }

        AuthParams authParams = JSON.parseObject(s, AuthParams.class);
        Auth bean = (Auth) applicationContext.getBean(authParams.getMethod());
        com.hls.base.po.User auth = bean.auth(authParams);
        String jsonString = JSONObject.toJSONString(auth);
        return User.withUsername(jsonString)
                .password(auth.getPasswd())
                .authorities(auth.getAccess())
                .build();
    }
}
