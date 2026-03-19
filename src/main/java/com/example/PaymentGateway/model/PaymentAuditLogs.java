package com.example.PaymentGateway.model;


import com.example.PaymentGateway.enums.PaymentStatus;
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
public class PaymentAuditLogs {

    @Id
    Long id ;

    @ManyToOne
    @JoinColumn(name="payment_id")
    private Payment payment;


    @Enumerated(EnumType.STRING)
    private PaymentStatus oldStatus;

    @Enumerated(EnumType.STRING)
    private PaymentStatus newStatus;

    String changedBy;

    LocalDateTime changedAt;
}
