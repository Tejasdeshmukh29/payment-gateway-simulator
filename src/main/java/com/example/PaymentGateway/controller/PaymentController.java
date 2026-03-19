package com.example.PaymentGateway.controller;


import com.example.PaymentGateway.dto.PaymentRequestDTO;
import com.example.PaymentGateway.model.Payment;
import com.example.PaymentGateway.repository.PaymentRepository;
import com.example.PaymentGateway.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaymentRepository paymentRepository;

    @PostMapping("/create_payment")
    public ResponseEntity<?> createPayment(@Valid @RequestBody PaymentRequestDTO request, @RequestHeader ("Idempotency-Key") String key)
    {
        return ResponseEntity.ok(paymentService.createPayment(request,key));
    }

    // testing for payment
    @GetMapping("/{id}/viewPayment")
    public ResponseEntity<Payment> showPayment(@PathVariable long id)
    {
        return ResponseEntity.ok().body(paymentRepository.getById(id));
    }
}
