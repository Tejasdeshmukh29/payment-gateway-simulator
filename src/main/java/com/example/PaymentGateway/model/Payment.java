package com.example.PaymentGateway.model;

import com.example.PaymentGateway.enums.PaymentStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@Entity
@NoArgsConstructor
@Getter
@Setter
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;


    @PositiveOrZero
    BigDecimal amount;


    @NotBlank
    String currency;

    @Column(unique = true)
    String idempotencyKey;

    LocalDateTime createdAt;
    LocalDateTime updatedAt;

    String description;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;


    @PrePersist  // @PrePersitant tell jpa that Run this method automatically BEFORE the entity is inserted into the database.
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}

