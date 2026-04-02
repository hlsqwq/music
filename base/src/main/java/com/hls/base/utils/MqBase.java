package com.hls.base.utils;


import com.hls.base.MusicCd;
import com.hls.base.config.MqConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class MqBase {


    private final RabbitTemplate rabbitTemplate;


    /**
     * 其他服务删除媒资文件
     * @param mediaId 媒资id
     * @param mediaUrl 媒资url
     */
    public void sendMessageDelMedia(Integer mediaId, String mediaUrl) {
        String substring = mediaUrl.substring(mediaUrl.indexOf("/") + 1);
        substring = substring.substring(substring.indexOf("/") + 1);
        sendMessageToMusic(MqConfig.MEDIA_TEMP_KEY,
                new com.hls.base.dto.DelTempMedia(mediaId, null, "music", substring));
    }

    public void sendMessage(String exchange, String routingKey, Object message, MusicCd cd) {
        rabbitTemplate.convertAndSend(exchange, routingKey, message, cd);
    }

    public void sendMessage(String exchange, String routingKey, Object message) {
        MusicCd musicCd = new MusicCd(0, exchange, routingKey, message);
        musicCd.setId(UUID.randomUUID().toString());
        sendMessage(exchange, routingKey, message, musicCd);
    }

    public void sendMessageToMusic(String routingKey, Object message) {
        sendMessage(MqConfig.EXCHANGE, routingKey, message);
    }

    /**
     *
     * @param message 第一个参数是 MusicCd    第二个 cause
     */
    public void sendMessageToDead(Object... message) {
        sendMessage(MqConfig.DEAD_EXCHANGE, MqConfig.DEAD_KEY, message);
    }


    public void sendDelayMessage(String exchange, String routingKey, Object message, MusicCd cd, int delay) {
        rabbitTemplate.convertAndSend(exchange, routingKey, message, message1 -> {
            message1.getMessageProperties().setDelay(delay);
            return message1;
        }, cd);
    }


    public void sendDelayMessage(String exchange, String routingKey, Object message, int delay) {
        MusicCd musicCd = new MusicCd(0, exchange, routingKey, message);
        musicCd.setId(UUID.randomUUID().toString());
        sendDelayMessage(exchange, routingKey, message, musicCd, delay);
    }


    public void sendDelayMessageToMusic(String routingKey, Object message, int delay) {
        sendDelayMessage(MqConfig.EXCHANGE, routingKey, message, delay);
    }

    public void sendDelayMessage(String exchange, String routingKey, Object message) {
        sendDelayMessage(exchange, routingKey, message, 1000);
    }

    /**
     * 发送消息延迟一秒
     *
     * @param routingKey 队列 key
     * @param message    消息
     */
    public void sendDelayMessageToMusic(String routingKey, Object message) {
        sendDelayMessage(MqConfig.EXCHANGE, routingKey, message, 1000);
    }

    /**
     * 发送消息延迟一小时
     *
     * @param routingKey 队列 key
     * @param message    消息
     */
    public void sendDelayHourMessageToMusic(String routingKey, Object message) {
        sendDelayMessage(MqConfig.EXCHANGE, routingKey, message, 1000 * 60 * 60);
    }

}
