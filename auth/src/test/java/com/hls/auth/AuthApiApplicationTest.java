package com.hls.auth;


import cn.hutool.core.lang.Pair;
import com.hls.auth.config.TokenConfig;
import lombok.RequiredArgsConstructor;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;

import java.security.KeyPair;

@SpringBootTest
public class AuthApiApplicationTest {

    @Autowired
    private TokenConfig tokenConfig;

    @Test
    public void keyPair() {
        Pair<String, String> keyPair = TokenConfig.generateRsaKey();
        System.out.println(keyPair.getKey());
        System.out.println(keyPair.getValue());
    }
}
