package com.hls.media.mq;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hls.base.config.MqConfig;
import com.hls.base.exception.MusicException;
import com.hls.base.utils.RedisBase;
import com.hls.base.dto.DelTempMedia;
import com.hls.media.dto.FileCheckState;
import com.hls.media.po.Media;
import com.hls.media.po.UserMedia;
import com.hls.media.service.IMediaService;
import com.hls.media.service.IUserMediaService;
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

import javax.lang.model.type.PrimitiveType;

@Slf4j
@RequiredArgsConstructor
@Component
public class MediaHandle {


    private final MinioClient minioClient;
    private final RedisBase redisBase;
    private final IUserMediaService userMediaService;
    private final IMediaService mediaService;

    @RabbitListener(bindings = @QueueBinding(value = @Queue(name = MqConfig.MEDIA_TEMP_QUEUE),
            exchange = @Exchange(name = MqConfig.EXCHANGE_DELAY, type = ExchangeTypes.DIRECT, delayed = "true"),
            key = {MqConfig.MEDIA_TEMP_KEY}))
    public void delTempMedia(DelTempMedia delTempMedia) {
        if (delTempMedia.getMd5() != null) {
            //media根据md5判断是否被使用
            LambdaQueryWrapper<Media> eq = new LambdaQueryWrapper<Media>()
                    .eq(Media::getMd5, delTempMedia.getMd5());
            Media one = mediaService.getOne(eq);
            if (one == null || one.getRefCount() < 1) {
                del(delTempMedia);
            }
        } else if (delTempMedia.getUserMediaId() != null) {
            //删除用户资源
            Integer del = userMediaService.del(delTempMedia.getUserMediaId());
            if (del < 1) {
                del(delTempMedia);
            }
        } else {
            MusicException.cast("删除文件错误");
        }
    }

    private void del(DelTempMedia delTempMedia) {
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
