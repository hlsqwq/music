package com.hls.auth.config;


import com.hls.auth.po.LoginSuccessDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Slf4j
@RequiredArgsConstructor
@Component
public class TokenUtils {

    private final JwtEncoder jwtEncoder;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    public LoginSuccessDto getToken(Authentication authentication) {
        String authorities = authentication.getAuthorities().toString();
        authorities = authorities.replace("[", "").replace("]", "");

        Instant now = Instant.now();
        long expiry = 3600L; // 1小时有效期

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("yixiao") // 签发者
                .issuedAt(now)             // 签发时间
                .expiresAt(now.plusSeconds(expiry)) // 过期时间
                .subject(authentication.getName())  // 用户名
                .claim("access", authorities)
                .build();

        // 2. 使用 JwtEncoder 进行签名生成 Token
        String token = this.jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

        // 3. 构建返回给前端的 JSON 数据
        LoginSuccessDto loginSuccessDto = new LoginSuccessDto();
        loginSuccessDto.setToken(token);
        loginSuccessDto.setCode(200);
        loginSuccessDto.setMessage("ok");
        loginSuccessDto.setExpires(expiry);
        loginSuccessDto.setToken_type("Bearer");
        return loginSuccessDto;
    }



}
