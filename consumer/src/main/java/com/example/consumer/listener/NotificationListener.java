package com.example.consumer.listener;

import com.example.consumer.service.DeliveryProcessor;
import com.example.producer.dto.NotificationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationListener {

    private final DeliveryProcessor processor;

    @RabbitListener(queues = "${rabbitmq.queue.name}")
    public void onMessage(com.example.producer.dto.NotificationRequest msg) {
        processor.process(msg.getType(), msg.getRecipient(), msg.getBody());
    }
}
