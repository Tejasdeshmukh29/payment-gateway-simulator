package com.example.PaymentGateway.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@RequestMapping("/merchant")
@RestController
public class MerchantWebhookController {

    private static final String SECRET = "my-webhook-secret-key-123";

    @PostMapping("/webhook")
    public ResponseEntity<String> recivedWebhook(@RequestHeader("X-Signature") String receivedSignature,
                                                 @RequestBody String payload)
    {
        // this returns final(HMAC_SHA256(secret_key, payload))Hmac string that we can compare with Hmac string comming from request
        String genratedSignature = generateSignature(payload);

        if(!genratedSignature.equals(receivedSignature)) return ResponseEntity.status(403).body("Invalied Signature ");

        System.out.println("webhook verified successfully !");
        System.out.println(payload);

        return ResponseEntity.ok("webhook verified Successfully !");
    }


    public String generateSignature(String payload)
    {
        try{
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec key = new SecretKeySpec(SECRET.getBytes(),"HmacSHA256");

            mac.init(key);
            byte[] rawHmac =  mac.doFinal(payload.getBytes());
            return Base64.getEncoder().encodeToString(rawHmac);

        }catch(Exception e)
        {
            throw new RuntimeException("failed to genrate Hmac");
        }
    }




    // genrating x-signature
    @PostMapping("/test-signature")
    public String generateTestSignature(@RequestBody String payload) {
        return generateSignature(payload);
    }

}
