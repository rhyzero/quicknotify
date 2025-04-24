package com.example.consumer.service;

import com.example.consumer.model.DeliveryLog;
import com.example.consumer.repository.DeliveryLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeliveryProcessor {

    private final DeliveryLogRepository repo;

    /**
     * Pretend to “send” the notification.
     * Throwing an exception triggers RabbitMQ retry & DLQ routing.
     */
    public void process(String type, String recipient, String body) {

        boolean delivered = !recipient.contains("fail");

        repo.save(DeliveryLog.builder()
                .type(type)
                .recipient(recipient)
                .status(delivered ? "SUCCESS" : "FAILED")
                .retryCount(0)
                .build());

        if (!delivered) {
            throw new IllegalStateException("Simulated send failure");
        }
    }
}
