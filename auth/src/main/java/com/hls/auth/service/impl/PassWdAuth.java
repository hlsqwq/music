package com.hls.auth.service.impl;

import com.hls.auth.po.AuthParams;
import com.hls.auth.po.User;
import com.hls.auth.service.Auth;
import com.hls.auth.service.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Slf4j
@RequiredArgsConstructor
@Service(value = "passwd")
public class PassWdAuth implements Auth {

    private final IUserService userService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User auth(AuthParams authParams) {
        User byId = userService.getById(1);
        byId.setPasswd(passwordEncoder.encode(authParams.getPassword()));
        log.info(byId.getPasswd());
        return byId;
    }
}
