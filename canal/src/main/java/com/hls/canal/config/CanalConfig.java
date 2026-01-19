package com.hls.canal.config;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetSocketAddress;

/**
 * Canal连接器配置类
 */
@Configuration
public class CanalConfig {

    @Value("${canal.server}")
    private String canalServer;

    @Value("${canal.destination}")
    private String destination;

    @Value("${canal.username}")
    private String username;

    @Value("${canal.password}")
    private String password;

    /**
     * 创建Canal连接器Bean（单机版）
     */
    @Bean
    public CanalConnector canalConnector() {
        // 解析Canal服务端地址（格式：ip:port）
        String[] serverArr = canalServer.split(":");
        String host = serverArr[0];
        int port = Integer.parseInt(serverArr[1]);
        
        // 创建单机版Canal连接器
        CanalConnector connector = CanalConnectors.newSingleConnector(
                new InetSocketAddress(host, port),
                destination,
                username,
                password
        );
        return connector;
    }
}