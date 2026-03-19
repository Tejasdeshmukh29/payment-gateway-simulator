package com.example.PaymentGateway.controller;


import com.example.PaymentGateway.service.PaymentAuthorizationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payments")
public class PaymentAuthorizationController {

    @Autowired
    private PaymentAuthorizationService paymentAuthorizationService;

    @PostMapping("/{paymentId}/authorize")
    public ResponseEntity<?>  authorize(@Valid  @PathVariable Long paymentId)
    {
         return ResponseEntity.ok(paymentAuthorizationService.authorizePayment(paymentId));
    }
}
