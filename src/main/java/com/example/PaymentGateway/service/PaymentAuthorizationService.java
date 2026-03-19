package com.example.PaymentGateway.service;


import com.example.PaymentGateway.dto.PaymentAuthorizationResponseDTO;
import com.example.PaymentGateway.dto.WebhookPayloadDTO;
import com.example.PaymentGateway.enums.PaymentStatus;
import com.example.PaymentGateway.enums.TransactionStatus;
import com.example.PaymentGateway.enums.TransactionType;
import com.example.PaymentGateway.exception.InsufficientBalanceException;
import com.example.PaymentGateway.exception.InvalidRequestException;
import com.example.PaymentGateway.exception.PaymentStateIsNotValid;
import com.example.PaymentGateway.model.Payment;
import com.example.PaymentGateway.model.Transaction;
import com.example.PaymentGateway.model.User;
import com.example.PaymentGateway.model.Wallet;
import com.example.PaymentGateway.repository.PaymentRepository;
import com.example.PaymentGateway.repository.TransactionRespository;
import com.example.PaymentGateway.repository.WalletRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Transactional
public class PaymentAuthorizationService {

    //  day 6 tasks
    //  make changes in wallet service allow debit only if payment in authorized state
    //  deduct money from blocked funds in wallet not from account payment CREATED --> AUTHORIZED --> PROCESSING-->CAPTURE

    //   payemnt authorization service
    //   load payment
    //   validate status == created
    //   load wallet
    //   check balance is sufficent or not
    //   block amount and save wallet

    // cases to be consider
    //✔ authorize with insufficient balance → fail
    //✔ capture twice → fail
    //✔ capture after refund → fail
    //✔ authorize twice → fail


    // day 10 adding webhook logic at every point where payment status get changed (here for authorize state )


    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private TransactionRespository transactionRespository;

    @Autowired
    private WebhookService webhookService;


    public PaymentAuthorizationResponseDTO authorizePayment(Long paymentId) {

        // loading wallet and payments
        Payment payment = paymentRepository.findById(paymentId).orElseThrow(() -> new InvalidRequestException("payment id found to be incorrect at a time of authorization "));
        User user = payment.getUser();
        Wallet wallet = walletRepository.findByUserId(user.getId()).orElseThrow(() -> new InvalidRequestException("User id found to be incorrect at a time of authorization"));

        // check for double Authorization if payment already in authorize state
        if(payment.getStatus() == PaymentStatus.AUTHORIZED) throw  new PaymentStateIsNotValid(" Payment already in Authorize state ");


        // validating status of payment
        if (payment.getStatus() != PaymentStatus.CREATED)
            throw new PaymentStateIsNotValid("Payment must be in CREATED state to authorize");

        //checking account balance is  sufficent or not to add amount in blocked amount
        BigDecimal available = wallet.getBalance().subtract(wallet.getBlockedAmount()); // balance required for transaction = balance - blocked amount
        if (available.compareTo(payment.getAmount()) < 0)
            throw new InsufficientBalanceException("your balance is insufficent for payment");

        // now balance is sufficent for a converting into blocking state
        wallet.setBlockedAmount(wallet.getBlockedAmount().add(payment.getAmount()));

        // updating payment status & sending new webhook for authorized state
        payment.setStatus(PaymentStatus.AUTHORIZED);
        WebhookPayloadDTO payload = new WebhookPayloadDTO(paymentId,payment.getAmount(),payment.getStatus().toString() ,payment.getCurrency());
        webhookService.sendWebhook(payload,payment);

        // saving wallet and payment
        paymentRepository.save(payment);
        walletRepository.save(wallet);

        //cretaing transaction for it
        Transaction tx = new Transaction(payment, payment.getAmount(), TransactionStatus.SUCCESS, TransactionType.HOLD);
        transactionRespository.save(tx);

        return new PaymentAuthorizationResponseDTO(paymentId, PaymentStatus.AUTHORIZED, wallet.getBlockedAmount());
    }

}
