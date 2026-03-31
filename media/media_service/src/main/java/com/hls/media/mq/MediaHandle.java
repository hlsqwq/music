package com.hls.media.mq;

import com.hls.base.config.MqConfig;
import com.hls.base.utils.RedisBase;
import com.hls.media.dto.DelTempMedia;
import io.minio.MinioClient;
import io.minio.RemoveObjectArgs;
import kotlin.Pair;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class MediaHandle {


    private final MinioClient minioClient;
    private final RedisBase redisBase;

    @RabbitListener(bindings = @QueueBinding(value = @Queue(name = MqConfig.MEDIA_TEMP_QUEUE),
            exchange = @Exchange(name = MqConfig.EXCHANGE_DELAY, type = ExchangeTypes.DIRECT, delayed = "true"),
            key = {MqConfig.MEDIA_TEMP_KEY}))
    public void delTempMedia(DelTempMedia delTempMedia) {
        if (!redisBase.get(delTempMedia.getKey(), Pair.class).getSecond().equals("ok")) {
            try {
                minioClient.removeObject(RemoveObjectArgs.builder()
                        .bucket(delTempMedia.getBucketName())
                        .object(delTempMedia.getFilePath())
                        .build());
            } catch (Exception e) {
                log.error("删除文件失败：path:{},{}", delTempMedia.getFilePath(), e.getMessage());
            }
        }
    }


}
