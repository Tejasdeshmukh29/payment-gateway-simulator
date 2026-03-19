package com.example.PaymentGateway.exception;

public class IncorrectPassword extends RuntimeException {
    public IncorrectPassword(String msg)
    {
        super(msg);
    }
}
