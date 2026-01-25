package com.hls.base.config;


import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.lang.reflect.ParameterizedType;


@Slf4j
@RequiredArgsConstructor
@Configuration
public class messageConfig {

    private final RabbitTemplate rabbitTemplate;


    @PostConstruct
    public void init(){
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if(!(correlationData instanceof com.hls.base.MusicCd musicCd)){
                log.error("correlationData is not a MusicCd");
                return;
            }
            String msgId = musicCd.getId().isEmpty() ? "未知" : musicCd.getId();
            if (ack) {
                log.info("消息[ {} ]发送成功", msgId);
            }else{
                log.info("消息[ {} ]发送失败原因：{}", msgId,cause);
                retry(musicCd,cause);
            }
        });


        rabbitTemplate.setReturnsCallback(returnedMessage -> {
            log.error("message:{}",returnedMessage.getMessage());
            log.error("routingKey:{}",returnedMessage.getRoutingKey());
            log.error("exchange:{}",returnedMessage.getExchange());
            log.error("errorCode:{}",returnedMessage.getReplyCode());
            log.error("errorText:{}",returnedMessage.getReplyText());
        });
    }

    private void retry(com.hls.base.MusicCd cd, String cause) {
        cd.add();
        if (cd.isBeyond()) {
            log.error("消息[{}]重试3次失败，发送死信", cd.getId());
            //todo
            return;
        }


        try {
            Thread.sleep(1000);
            rabbitTemplate.convertAndSend(
                    cd.getExchange(),
                    cd.getRoutingKey(),
                    cd.getMessage(),
                    cd
            );
            log.info("消息[{}]第{}次重试发送", cd.getId(), cd.getRetryCount());
        } catch (Exception e) {
            log.error("重试发送失败：{}", e.getMessage());
        }
    }



}
