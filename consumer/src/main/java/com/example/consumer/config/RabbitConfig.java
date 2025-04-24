package com.example.consumer.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RabbitConfig {

    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;
    @Value("${rabbitmq.queue.name}")
    private String queue;
    @Value("${rabbitmq.messaging.dlq}")
    private String dlqName;

    @Bean public Exchange notificationExchange() {
        return ExchangeBuilder.topicExchange(exchangeName).durable(true).build();
    }

    @Bean 
    public Queue notificationQueue() {
        return QueueBuilder
                .durable(queue)   
                .build();                  
    }

    @Bean public Queue deadLetterQueue() {
        return QueueBuilder.durable(dlqName).build();
    }

    @Bean public Binding binding(Queue notificationQueue, Exchange notificationExchange,
                                 @Value("${rabbitmq.routingKey.name}") String routingKey) {
        return BindingBuilder.bind(notificationQueue).to(notificationExchange).with(routingKey).noargs();
    }

    @Bean public MessageConverter messageConverter(ObjectMapper mapper) {
        return new Jackson2JsonMessageConverter(mapper);
    }
}
