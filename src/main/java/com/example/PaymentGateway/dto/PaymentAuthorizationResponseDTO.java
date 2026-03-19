package com.example.PaymentGateway.dto;


import com.example.PaymentGateway.enums.PaymentStatus;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaymentAuthorizationResponseDTO {

    private  Long paymentId;
    private PaymentStatus paymentStatus;
    private BigDecimal blockedAmount;
    private LocalDateTime timeStamp;

    public PaymentAuthorizationResponseDTO(Long paymentId, PaymentStatus paymentStatus, @PositiveOrZero BigDecimal blockedAmount) {
        this.paymentId = paymentId;
        this.paymentStatus = paymentStatus;
        this.blockedAmount = blockedAmount;
        this.timeStamp = LocalDateTime.now();
    }


}
