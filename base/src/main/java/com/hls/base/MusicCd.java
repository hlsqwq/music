package com.hls.base;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.amqp.rabbit.connection.CorrelationData;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class MusicCd extends CorrelationData {

    private int retryCount = 0;
    private String exchange;
    private String routingKey;
    private Object message;

    public void add(){
        this.retryCount+=1;
    }

    public boolean isBeyond(){
        return retryCount>3;
    }

}
