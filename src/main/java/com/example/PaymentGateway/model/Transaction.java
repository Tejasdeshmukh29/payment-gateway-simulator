package com.example.PaymentGateway.model;

import com.example.PaymentGateway.enums.TransactionStatus;
import com.example.PaymentGateway.enums.TransactionType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@NoArgsConstructor
@Entity
@AllArgsConstructor
@Getter
@Setter
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name="payment_id")
    private Payment payment;


    @Positive
    BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    LocalDateTime createdAt;

    public Transaction(Payment payment, @PositiveOrZero BigDecimal amount, TransactionStatus transactionStatus, TransactionType transactionType) {
        this.payment = payment;
        this.amount = amount;
        this.status = transactionStatus;
        this.type = transactionType;
    }

    @PrePersist
    public void CreatedAt() {
        createdAt = LocalDateTime.now();
    }
}
