package com.hls.base.utils;


import com.hls.base.MusicCd;
import com.hls.base.config.mqConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class mqUtils {

    private final RabbitTemplate rabbitTemplate;

    public void delMedia(String url) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("type", "delUrl");
        map.put("url", url);
        MusicCd musicCd = new MusicCd(0, mqConfig.EXCHANGE, mqConfig.MEDIA_KEY, map);
        musicCd.setId(UUID.randomUUID().toString());
        rabbitTemplate.convertAndSend(mqConfig.EXCHANGE, mqConfig.MEDIA_KEY, map, musicCd);
    }

    public void addMedia(String url) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("type", "addUrl");
        map.put("url", url);
        MusicCd musicCd = new MusicCd(0, mqConfig.EXCHANGE, mqConfig.MEDIA_KEY, map);
        musicCd.setId(UUID.randomUUID().toString());
        rabbitTemplate.convertAndSend(mqConfig.EXCHANGE, mqConfig.MEDIA_KEY, map, musicCd);
    }
}
