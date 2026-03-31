package com.hls.base.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.amqp.rabbit.retry.RepublishMessageRecoverer;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;


@Component
public class deadExchageConfig {

    @Bean
    public DirectExchange exchange() {
        return ExchangeBuilder.directExchange(MqConfig.DEAD_EXCHANGE).build();
    }

    @Bean
    public Queue queue() {
        return QueueBuilder.durable(MqConfig.DEAD_QUEUE).build();
    }

    @Bean
    public Binding bind(Queue queue, DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(MqConfig.DEAD_KEY);
    }


    @Bean
    public MessageRecoverer messageRecoverer(RabbitTemplate rabbitTemplate) {
        return new RepublishMessageRecoverer(rabbitTemplate, MqConfig.DEAD_EXCHANGE, MqConfig.DEAD_KEY);
    }
}
