package com.example.consumer.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity @Table(name = "delivery_log")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DeliveryLog {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type;
    private String recipient;
    private String status;
    private int    retryCount;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();
}
