package com.example.PaymentGateway.controller;


import com.example.PaymentGateway.service.WalletService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/payments")
public class WalletController {

    @Autowired
    private WalletService walletService;

    @PostMapping("/{paymentId}/capture")
    public ResponseEntity<?> debitAmount(@PathVariable Long paymentId)
    {
       return ResponseEntity.ok(walletService.capturePayment(paymentId));
    }

}
