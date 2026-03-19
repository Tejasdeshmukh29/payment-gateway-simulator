package com.example.PaymentGateway.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentCaptureResponseDTO {
    private Long paymentId;
    private String status;
    private BigDecimal remainingBalance;

    public PaymentCaptureResponseDTO(Long paymentId, String status, BigDecimal balance) {
        this.paymentId = paymentId;
        this.status = status;
        this.remainingBalance = balance;
    }

}
