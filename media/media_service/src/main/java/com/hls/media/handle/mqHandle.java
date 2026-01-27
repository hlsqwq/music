package com.hls.media.handle;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hls.base.config.mqConfig;
import com.hls.base.exception.MusicException;
import com.hls.media.config.FileTranscodeManager;
import com.hls.media.config.MinioConfig;
import com.hls.media.po.Media;
import com.hls.media.po.MediaTemp;
import com.hls.media.service.IMediaService;
import com.hls.media.service.IMediaTempService;
import io.minio.MinioClient;
import io.minio.RemoveObjectArgs;
import io.minio.StatObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;


@Slf4j
@RequiredArgsConstructor
@Component
public class mqHandle {

    private final MinioClient minioClient;
    private final MinioConfig minioConfig;
    private final IMediaTempService mediaTempService;
    private final IMediaService mediaService;
    private final FileTranscodeManager fileTranscodeManager;

    @RabbitListener(bindings = @QueueBinding(value = @Queue(name = mqConfig.MEDIA_QUEUE),
            exchange = @Exchange(name = mqConfig.EXCHANGE, type = ExchangeTypes.DIRECT),
            key = {mqConfig.MEDIA_KEY}))
    public void mediaQueue(Map<String, String> message) throws Exception {
        log.info("接收到消息：{}", message);
        if (message == null || message.isEmpty()) {
            return;
        }
        String type = message.get("type");
        String url = message.get("url");
        if(url==null || url.isEmpty()){
            return;
        }
        String s1 = url.substring(url.indexOf("/"));
        switch (type) {
            case "delUrl"->{
                LambdaQueryWrapper<Media> q = new LambdaQueryWrapper<Media>()
                        .eq(Media::getUrl, url);
                Media one1 = mediaService.getOne(q);
                if(one1==null){
                    return;
                }
                one1.setRefNum(one1.getRefNum()-1);
                if(one1.getRefNum()!=0){
                    mediaService.updateById(one1);
                    return;
                }else{
                    mediaService.removeById(one1.getId());
                }
                try {
                    minioClient.statObject(StatObjectArgs.builder()
                            .object(s1)
                            .bucket(minioConfig.music)
                            .build());
                } catch (Exception e) {
                    return;
                }
                minioClient.removeObject(RemoveObjectArgs.builder()
                        .bucket(minioConfig.music)
                        .object(s1).build());
                mediaService.del(url);
            }
            case "addUrl"->{
                LambdaQueryWrapper<Media> q = new LambdaQueryWrapper<Media>()
                        .eq(Media::getUrl, url);
                Media one1 = mediaService.getOne(q);
                if(one1 != null){
                    one1.setRefNum(one1.getRefNum()+1);
                    mediaService.updateById(one1);
                    return;
                }
                LambdaQueryWrapper<MediaTemp> qw = new LambdaQueryWrapper<MediaTemp>()
                        .eq(MediaTemp::getUrl, url);
                MediaTemp one = mediaTempService.getOne(qw);
                if (one == null) {
                    return;
                }
                //转正
                String s = mediaService.checkFile(one.getUserId(), one.getMd5(), one.getFileName());
                if(!s.equals("ok")){
                    log.error("没有这个文件,md5:{},fileName:{}",one.getMd5(),one.getFileName());
                    MusicException.cast("没有这个文件");
                }

                fileTranscodeManager.autoTranscode(minioConfig.temp, one.getPath(),
                        minioConfig.music, one.getPath());


                Media media = BeanUtil.copyProperties(one, Media.class);
                media.setBucket(minioConfig.music);
                media.setCreateTime(LocalDateTime.now());
                mediaService.add(media);
            }



        }
    }


}
