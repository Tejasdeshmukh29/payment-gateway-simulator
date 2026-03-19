package com.example.PaymentGateway.model;


import com.example.PaymentGateway.enums.WebhookStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class WebhookLogs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name="payment_id")
    private Payment payment;


    String webhookUrl;

    @Enumerated(EnumType.STRING)
    private WebhookStatus status;

    int attemptCount;

    LocalDateTime lastAttemptAt;

    LocalDateTime createdAt;
}
