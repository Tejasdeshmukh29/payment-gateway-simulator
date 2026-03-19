package com.example.PaymentGateway.controller;


import com.example.PaymentGateway.model.User;
import com.example.PaymentGateway.model.Wallet;
import com.example.PaymentGateway.repository.UserRepository;
import com.example.PaymentGateway.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EntiresAdding {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WalletRepository walletRepository;

    @GetMapping("/user/addnew")
    public ResponseEntity<String> addUser(@RequestBody User user)
    {
        userRepository.save(user);
        return ResponseEntity.ok("user created successfuly");
    }

    @GetMapping("/wallet/addnew")
    public ResponseEntity<String> addwalletentery(@RequestBody Wallet wallet)
    {
        walletRepository.save(wallet);
        return ResponseEntity.ok("new entery added in wallet ");
    }

    @GetMapping("/hello")
    public String hello()
    {
       return "hello api";
    }



}
