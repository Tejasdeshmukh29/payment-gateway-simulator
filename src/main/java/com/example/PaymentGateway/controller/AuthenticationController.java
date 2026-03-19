package com.example.PaymentGateway.controller;


import com.example.PaymentGateway.dto.UserLoginDTO;
import com.example.PaymentGateway.dto.UserRegisterDTO;
import com.example.PaymentGateway.service.SecurityAuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private SecurityAuthenticationService securityAuthenticationService;

    public AuthenticationController(SecurityAuthenticationService securityAuthenticationService) {
        this.securityAuthenticationService = securityAuthenticationService;
    }

    @PostMapping("/login")
    public String login(@RequestBody UserLoginDTO userLoginDTO)
    {
       return securityAuthenticationService.authenticate(userLoginDTO);
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserRegisterDTO userRegisterDTO)
    {
        securityAuthenticationService.Register(userRegisterDTO);
        return ResponseEntity.ok("User registered succesfully");
    }

}
