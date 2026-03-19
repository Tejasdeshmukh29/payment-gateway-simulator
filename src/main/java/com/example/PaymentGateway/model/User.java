package com.example.PaymentGateway.model;



import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @NotBlank
    String name;

    @NotBlank
    String password;

    @Email
    @Column(unique = true)
    String email;

    @Size(min = 10, max = 10)
    @Pattern(regexp = "\\d{10}")
    String phoneNo;

    LocalDateTime createdAt;


    @PrePersist  // @PrePersitant tell jpa that Run this method automatically BEFORE the entity is inserted into the database.
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }


}
