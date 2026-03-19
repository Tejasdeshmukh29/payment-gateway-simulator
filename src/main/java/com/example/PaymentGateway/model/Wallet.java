package com.example.PaymentGateway.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long walletId;

    @PositiveOrZero
    @Column(nullable = false)
    BigDecimal balance = BigDecimal.ZERO;


    @PositiveOrZero
    BigDecimal blockedAmount = BigDecimal.ZERO;

    @OneToOne
    @JoinColumn(name="user_id")
    private User user;

    @Version
    private int version;


    LocalDateTime updatedAt;


    @PreUpdate
    void onUpdate()
    {
        updatedAt = LocalDateTime.now();
    }

}
