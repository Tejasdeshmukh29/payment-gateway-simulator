package com.example.PaymentGateway.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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
public class IdempotencyKey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    String requestHash;

    @Column(unique = true)
    @NotBlank
    String idempotencyKey;

    String responseBody;

    LocalDateTime createdAt;

    @PrePersist
    public void created()
    {
        this.createdAt = LocalDateTime.now();
    }

}
