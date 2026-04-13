package com.hls.auth.controller;


import com.alibaba.fastjson2.JSON;
import com.hls.auth.config.TokenUtils;
import com.hls.auth.po.AuthParams;
import com.hls.auth.po.LoginSuccessDto;
import com.hls.base.R;
import com.hls.base.exception.MusicException;
import com.hls.base.po.User;
import com.nimbusds.jose.KeySourceException;
import com.nimbusds.jose.jwk.JWKMatcher;
import com.nimbusds.jose.jwk.JWKSelector;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.*;

import java.net.PasswordAuthentication;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * 认证接口控制器  有前缀   /auth
 */
@RequiredArgsConstructor
@Slf4j
@RestController
public class AuthController {

    // 注入认证管理器（在 SecurityConfig 中配置的）
    @Autowired
    private AuthenticationManager authenticationManager;
    private final TokenUtils tokenUtils;


    private final JWKSource<SecurityContext> jwkSource;

    /**
     * 自定义登录接口
     *
     * @param authParams 认证参数（包含 method、username、password 等）
     * @return 登录成功信息
     */
    @PostMapping("/login")
    public R<LoginSuccessDto> login(@RequestBody AuthParams authParams) throws Throwable {
        // 1. 封装认证请求参数（注意：这里的 principal 传入 AuthParams 的 JSON 字符串，对应 loadUserByUsername 的入参 s）
        String principal = JSON.toJSONString(authParams);
        // 凭证（密码，前后端分离中可以传空，因为我们的逻辑在 loadUserByUsername 中已处理，这里只是满足框架要求）
        String credentials = authParams.getPassword();

        // 2. 调用认证管理器完成认证（框架会自动调用 customUserDetailsService.loadUserByUsername 方法）
        Authentication authentication = null;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(principal, credentials)
            );
        } catch (BadCredentialsException badCredentialsException) {
            return R.failure("账号或者密码错误");
        } catch (InternalAuthenticationServiceException e) {
            Throwable cause = e.getCause();

            // 2. 提取错误信息（针对你 MusicException 的特殊结构）
            String message = "认证失败";
            if (cause instanceof MusicException) {
                // 既然 getMessage() 是 null，直接拿你的 errMessage 字段
                message = ((MusicException) cause).getErrMessage();
            } else if (cause != null) {
                message = cause.getMessage();
            }

            return R.failure(message);
        }


        //todo 存入redis
        return R.success(tokenUtils.getToken(authentication));
    }


    @GetMapping("/oauth2/jwks")
    public Map<String, Object> jwks() throws KeySourceException {
        JWKSelector jwkSelector = new JWKSelector(new JWKMatcher.Builder().build());
        return new JWKSet(jwkSource.get(jwkSelector, null)).toJSONObject();
    }
}