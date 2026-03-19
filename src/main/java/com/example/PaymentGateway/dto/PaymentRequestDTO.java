package com.example.PaymentGateway.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentRequestDTO {

    @NotNull
    private Long userId;

    @Positive
    private BigDecimal amount;

    @NotBlank
    private String currency;

    private String description;
}
