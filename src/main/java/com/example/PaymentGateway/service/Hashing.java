package com.example.PaymentGateway.service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.example.PaymentGateway.dto.PaymentRequestDTO;
import org.springframework.stereotype.Service;

@Service
public class Hashing {


    public String hash(PaymentRequestDTO requestDTO)
    {
        // making canonicalString
        StringBuilder hexHash = null;
        try {

            String desc = requestDTO.getDescription() == null ? "" : requestDTO.getDescription().trim();
            String curr = requestDTO.getCurrency() == null ? "" : requestDTO.getCurrency().trim().toUpperCase();


            String input =requestDTO.getUserId().toString() +"|"+requestDTO.getAmount().toString() +"|"+ curr +"|"+desc ;


            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] byteHash=  messageDigest.digest(input.getBytes());

            // converting byte hash to hex hax
            hexHash = new StringBuilder();
            for(byte b : byteHash)
            {
                hexHash.append(String.format("%02x",b));
            }
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("hash genration faild ",e);
        }
        return hexHash.toString();
    }



}
