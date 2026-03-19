package com.example.PaymentGateway.enums;

public enum PaymentStatus {
    CREATED,
    AUTHORIZED,
    CAPTURED,
    FAILED,
    PROCESSING,
    REFUNDED
}



// Payment state flow : Created --> authorize --> processing --> Capture/failed --> refund