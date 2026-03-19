package com.example.PaymentGateway.exception;

public class ResourceNotFoundException extends  RuntimeException{
    public ResourceNotFoundException(String msg) {
        super(msg);
    }
}
