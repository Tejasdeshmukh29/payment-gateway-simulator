package com.example.PaymentGateway.service;
import com.example.PaymentGateway.enums.BankResponseStatus;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;


// in this class we are making simulation of bank here we give 70 % chances of success and 20 % of failed and 10% for timeout

@Service
public class BankProcessor {

    public BankResponseStatus process()
    {
        int a = ThreadLocalRandom.current().nextInt(1, 11);
        if(a<=7) return BankResponseStatus.SUCCESS;  // 70 % chance of success

        else if(a>7 && a<=9) return BankResponseStatus.FAILED; // chance of failure

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return BankResponseStatus.TIMEOUT; // timeout
    }
}
