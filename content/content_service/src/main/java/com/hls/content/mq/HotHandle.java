package com.hls.content.mq;


import com.hls.base.config.MqConfig;
import com.hls.content.dto.HotDataDto;
import com.hls.content.po.Mv;
import com.hls.content.service.IAlbumService;
import com.hls.content.service.IMvService;
import com.hls.content.service.ISingerService;
import com.hls.content.service.ISongService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class HotHandle {

    private final ISingerService singerService;
    private final ISongService songService;
    private final IAlbumService albumService;
    private final IMvService mvService;

//    @RabbitListener(bindings = @QueueBinding(value = @Queue(name = mqConfig.HOT_SINGER_QUEUE),
//            exchange = @Exchange(name = mqConfig.EXCHANGE, type = ExchangeTypes.DIRECT),
//            key = {mqConfig.HOT_SINGER_KEY}))
//    public void singerHotHandle() {
//
//    }


    @RabbitListener(bindings = @QueueBinding(value = @Queue(name = MqConfig.HOT_SONG_QUEUE),
            exchange = @Exchange(name = MqConfig.EXCHANGE, type = ExchangeTypes.DIRECT), key = {
            MqConfig.HOT_SONG_KEY}))
    public void updateSongPlayHandle(List<HotDataDto> task) {
        songService.updateSongPlay(task);
    }

//    @RabbitListener(bindings = @QueueBinding(value = @Queue(name = mqConfig.HOT_ALBUM_QUEUE),
//            exchange = @Exchange(name = mqConfig.EXCHANGE, type = ExchangeTypes.DIRECT), key = {
//            mqConfig.HOT_ALBUM_KEY}))
//    public void albumHotHandle(Integer id) throws Exception {
//
//    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue(name = MqConfig.HOT_MV_QUEUE),
            exchange = @Exchange(name = MqConfig.EXCHANGE, type = ExchangeTypes.DIRECT), key = {
            MqConfig.HOT_MV_KEY}))
    public void mvHotHandle(@NonNull List<List<HotDataDto>> task) {
        HashMap<Integer,Mv>map=new HashMap<>();
        task.forEach(list->{
            list.forEach(hotDataDto->{
                Mv mv=map.containsKey(hotDataDto.getId())?map.get(hotDataDto.getId())
                        :mvService.getById(hotDataDto.getId());
                mv.setPlayNum(mv.getPlayNum()+ hotDataDto.getPlayNum());
                mv.setLikeNum(mv.getLikeNum() + hotDataDto.getLikeNum());
                map.put(mv.getId(),mv);
            });
        });
        mvService.updatePlayOrLike(map.values().stream().toList());
    }

}
