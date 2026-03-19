package com.example.PaymentGateway.repository;

import com.example.PaymentGateway.enums.WebhookStatus;
import com.example.PaymentGateway.model.WebhookLogs;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WebhookRepository  extends JpaRepository<WebhookLogs, Long> {
    List<WebhookLogs> findByStatus(WebhookStatus webhookStatus);
}
