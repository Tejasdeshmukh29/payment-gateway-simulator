package com.example.PaymentGateway.exception;

public class PaymentStateIsNotValid extends RuntimeException{
    public PaymentStateIsNotValid(String msg)
    {
        super(msg);
    }
}
