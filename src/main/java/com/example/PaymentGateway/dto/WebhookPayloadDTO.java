package com.example.PaymentGateway.dto;

import com.example.PaymentGateway.model.Payment;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class WebhookPayloadDTO {

    private Long paymentId;
    private BigDecimal amount;
    private String paymentStatus;
    private String currency;

}
