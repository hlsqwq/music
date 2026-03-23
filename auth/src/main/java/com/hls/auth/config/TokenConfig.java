package com.hls.auth.config;

import cn.hutool.core.lang.Pair;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Configuration
public class TokenConfig {

    @Value("${key.private}")
    private String privateKey;
    @Value("${key.public}")
    private String publicKey;


    /**
     * 1. 配置 JWKSource
     * 代替了原来的 TokenStore。它负责提供加密令牌所需的密钥对。
     */
    @Bean
    public JWKSource<SecurityContext> jwkSource() throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        // 1. 还原公钥 (X509 规范)
        byte[] pubBytes = Base64.getDecoder().decode(publicKey);
        X509EncodedKeySpec pubSpec = new X509EncodedKeySpec(pubBytes);
        RSAPublicKey publicKey = (RSAPublicKey) keyFactory.generatePublic(pubSpec);

        // 2. 还原私钥 (PKCS8 规范)
        byte[] privBytes = Base64.getDecoder().decode(privateKey);
        PKCS8EncodedKeySpec privSpec = new PKCS8EncodedKeySpec(privBytes);
        RSAPrivateKey privateKey = (RSAPrivateKey) keyFactory.generatePrivate(privSpec);

        // 3. 创建 JWK 实例
        // 注意：kid (Key ID) 建议固定，或者用公钥的哈希值，不要用 randomUUID，否则重启后 kid 变了网关缓存会失效
        RSAKey rsaKey = new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID("yixiao")
                .build();

        return new ImmutableJWKSet<>(new JWKSet(rsaKey));
    }


    @Bean
    public JwtEncoder jwtEncoder(JWKSource<SecurityContext> jwkSource) {
        return new NimbusJwtEncoder(jwkSource);
    }

    /**
     * 辅助工具：生成 RSA 密钥对
     * 生产环境下，建议从 keystore 文件加载，而不是每次启动都随机生成
     */
    public static Pair<String, String> generateRsaKey() {
        KeyPair keyPair;
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            keyPair = keyPairGenerator.generateKeyPair();
            String s = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
            String s1 = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
            return Pair.of(s, s1);
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

}