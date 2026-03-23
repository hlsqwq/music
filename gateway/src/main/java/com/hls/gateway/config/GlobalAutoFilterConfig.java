package com.hls.gateway.config;


import lombok.RequiredArgsConstructor;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Configuration
public class GlobalAutoFilterConfig {

    private final ReactiveJwtDecoder decode;
    private static final List<String>white;
    static {
        white = new ArrayList<>();
        white.add("/login");
        white.add("/captcha");
    }

    @Order(value = -100)
    @Bean
    public GlobalFilter globalFilter() {
        return (exchange, chain) -> {
            String path = exchange.getRequest().getURI().getPath();
            if (white.stream().anyMatch(path::contains)) {
                return chain.filter(exchange);
            }

            String token = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (token == null || !token.startsWith("Bearer ")) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
            token = token.substring(7);
            return decode.decode(token)
                    .flatMap(jwt -> {
                        // 校验通过，可以从 jwt 中获取 Claims（如用户 ID、权限）
                        String userId = jwt.getClaimAsString("sub"); // 假设 sub 是用户 ID

                        // 4. 将用户信息通过 Header 传递给下游微服务（mutate 模式）
                        ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                                .header("X-User-Id", userId)
                                //todo 权限获取
//                                .header("X-User-Roles", jwt.getClaimAsStringList("authorities")
//                                        .toString())
                                .build();

                        return chain.filter(exchange.mutate().request(mutatedRequest).build());
                    })
                    // 5. 异常处理：Token 过期、伪造等情况会进入 onErrorResume
                    .onErrorResume(e -> {
                        e.printStackTrace();
                        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                        return exchange.getResponse().setComplete();
                    });
        };


    }

}
