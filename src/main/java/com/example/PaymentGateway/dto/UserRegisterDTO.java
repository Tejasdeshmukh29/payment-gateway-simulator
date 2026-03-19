package com.example.PaymentGateway.dto;

import lombok.Data;

@Data
public class UserRegisterDTO {
    String name;
    String password;
    String email;
    String phoneNo;
}
