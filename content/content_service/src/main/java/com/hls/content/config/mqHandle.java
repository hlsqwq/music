package com.hls.content.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hls.base.config.mqConfig;
import com.hls.content.po.Singer;
import com.hls.content.po.Song;
import com.hls.content.po.WorksCategory;
import com.hls.content.service.ISingerHotService;
import com.hls.content.service.ISingerService;
import com.hls.content.service.ISongService;
import com.hls.content.service.IWorksCategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;


@Slf4j
@RequiredArgsConstructor
@Component
public class mqHandle {

    private IWorksCategoryService worksCategoryService;
    private ISingerService singerService;
    private ISongService songService;


    @RabbitListener(bindings = @QueueBinding(value = @Queue(name = mqConfig.HOT_SINGER_QUEUE),
            exchange = @Exchange(name = mqConfig.EXCHANGE, type = ExchangeTypes.DIRECT),
            key = {mqConfig.HOT_SINGER_KEY}))
    public void singerHotQueue(Integer id) throws Exception {
        log.info("接收到消息：{}", id);
        if (id == null) {
            return;
        }
        Singer byId = singerService.getById(id);
        LambdaQueryWrapper<WorksCategory> eq = new LambdaQueryWrapper<WorksCategory>()
                .eq(WorksCategory::getSongId, id);
        WorksCategory one = worksCategoryService.getOne(eq);
        if (one == null) {
            return;
        }
        one.setHot((long) (byId.getLikeNum()*0.5+byId.getPlayNum()*0.5));
        worksCategoryService.updateById(one);
    }


    @RabbitListener(queues = mqConfig.HOT_SONG_QUEUE)
    public void songHotQueue(Integer id) throws Exception {
        log.info("接收到消息：{}", id);
        if (id == null) {
            return;
        }
        Song byId = songService.getById(id);
        LambdaQueryWrapper<WorksCategory> eq = new LambdaQueryWrapper<WorksCategory>()
                .eq(WorksCategory::getSongId, id);
        WorksCategory one = worksCategoryService.getOne(eq);
        if (one == null) {
            return;
        }
        one.setHot((long) (byId.getLikeNum()*0.5+byId.getPlayNum()*0.5));
        worksCategoryService.updateById(one);
    }

}
