package com.hls.media.handle;

import com.hls.base.config.mqConfig;
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
public class mqHandle {



    @RabbitListener(bindings = @QueueBinding(value = @Queue(name = mqConfig.MEDIA_QUEUE),
    exchange = @Exchange(name = mqConfig.EXCHANGE,type = ExchangeTypes.DIRECT),
    key = {mqConfig.MEDIA_KEY}))
    public void delQueue(String message){
        log.info("接收到消息：{}",message);
    }


}
