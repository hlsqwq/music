package com.hls.auth.controller;


import com.alibaba.fastjson2.JSON;
import com.hls.auth.config.TokenUtils;
import com.hls.auth.po.AuthParams;
import com.hls.auth.po.LoginSuccessDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * 认证接口控制器（前后端分离登录）
 */
@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

    // 注入认证管理器（在 SecurityConfig 中配置的）
    @Autowired
    private AuthenticationManager authenticationManager;
    private final TokenUtils tokenUtils;

    /**
     * 自定义登录接口
     *
     * @param authParams 认证参数（包含 method、username、password 等）
     * @return 登录成功信息
     */
    @PostMapping("/login")
    public LoginSuccessDto login(@RequestBody AuthParams authParams) {
        // 1. 封装认证请求参数（注意：这里的 principal 传入 AuthParams 的 JSON 字符串，对应 loadUserByUsername 的入参 s）
        String principal = JSON.toJSONString(authParams);
        // 凭证（密码，前后端分离中可以传空，因为我们的逻辑在 loadUserByUsername 中已处理，这里只是满足框架要求）
        String credentials = authParams.getPassword();

        // 2. 调用认证管理器完成认证（框架会自动调用 customUserDetailsService.loadUserByUsername 方法）
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(principal, credentials)
        );
        return tokenUtils.getToken(authentication);
    }

    /**
     * 测试认证后的接口（需要登录后才能访问）
     */
    @PostMapping("/test/private")
    public String testPrivate() {
        return "这是需要认证后才能访问的私有接口！";
    }
}