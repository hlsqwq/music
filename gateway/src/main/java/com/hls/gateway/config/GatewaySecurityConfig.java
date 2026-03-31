package com.hls.gateway.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class GatewaySecurityConfig {

    @Bean
    @LoadBalanced // 关键：开启负载均衡识别能力
    public WebClient.Builder loadBalancedWebClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    public ReactiveJwtDecoder jwtDecoder(WebClient.Builder builder) {
        // 使用具备负载均衡能力的 builder 构建 WebClient
        WebClient webClient = builder.build();

        // 这里的地址必须匹配你 Nacos 中的服务名
        // 如果你的服务名是 auth，那就写 http://auth/...
        return NimbusReactiveJwtDecoder.withJwkSetUri("http://auth/auth/oauth2/jwks")
                .webClient(webClient) // 强制使用这个能识别 Nacos 的 webClient
                .build();
    }



    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http.csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges ->
                        exchanges.anyExchange().permitAll()
                )
                .build();
    }
}