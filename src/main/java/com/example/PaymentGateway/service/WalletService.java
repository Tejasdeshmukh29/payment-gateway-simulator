package com.example.PaymentGateway.service;


import com.example.PaymentGateway.dto.PaymentCaptureResponseDTO;
import com.example.PaymentGateway.dto.WebhookPayloadDTO;
import com.example.PaymentGateway.enums.BankResponseStatus;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class WalletService {

    //    day 5 tasks
    //    processPayment(paymentId)
    //    load payment
    //    validate status
    //    load wallet
    //    check balance
    //    debit wallet
    //    save wallet
    //    update payment status
    //    save transaction log

    // day 10 adding webhook logic at every point where payment status get changed


    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private TransactionRespository transactionRespository;

    @Autowired
    private BankProcessor bankProcessor;

    @Autowired WebhookService webhookService;

    public PaymentCaptureResponseDTO capturePayment(Long paymentId) {

        // getting payment entity from payment id
        Payment payment = paymentRepository.findById(paymentId).orElseThrow(() -> new InvalidRequestException("payment not found "));
        User user = payment.getUser();

        // checking if payment already in capture state
        if (payment.getStatus() == PaymentStatus.CAPTURED)
            throw new PaymentStateIsNotValid("payment already in CAPTURE state ");

        // checking payment status it must be in authorized state before capturing it
        if (payment.getStatus() != PaymentStatus.AUTHORIZED)
            throw new InvalidRequestException("Payment must be AUTHORIZED before capture  ");

        //loading wallet from payment id
        Wallet wallet = walletRepository.findByUserId(user.getId()).orElseThrow(() -> new InvalidRequestException("Wallet not found "));

        // checking blocked amount balance of user
        if (wallet.getBlockedAmount().compareTo(payment.getAmount()) < 0)
            throw new InsufficientBalanceException("Blocked Amount  is Insufficent");

        // setting payment state to processing we will not send webhook for processing state
        payment.setStatus(PaymentStatus.PROCESSING);
        paymentRepository.save(payment);

        // getting acceptance or failure from bank
        BankResponseStatus bankResponseStatus = bankProcessor.process();
        if (bankResponseStatus == BankResponseStatus.FAILED || bankResponseStatus == BankResponseStatus.TIMEOUT) {
            wallet.setBlockedAmount(wallet.getBlockedAmount().subtract(payment.getAmount()));
            payment.setStatus(PaymentStatus.FAILED);
            // sending webhook for payment status = failed
            WebhookPayloadDTO payload = new WebhookPayloadDTO(paymentId,payment.getAmount(),payment.getStatus().toString() ,payment.getCurrency());
            webhookService.sendWebhook(payload,payment);
        } else {
            // debit amount from wallet logic if we recive sucess from bank flow : we are not debiting any amount from balance until payment get successful we are just adding amount to blocked amount and manipulating it , if only transaction get successful then only we debit amount from balance , else if failed we only debit amount from blocked amount
            payment.setStatus(PaymentStatus.CAPTURED);
            wallet.setBlockedAmount(wallet.getBlockedAmount().subtract(payment.getAmount()));
            wallet.setBalance(wallet.getBalance().subtract(payment.getAmount()));
            // sending webhooks for payment status = Capture
            WebhookPayloadDTO payload = new WebhookPayloadDTO(paymentId,payment.getAmount(),payment.getStatus().toString() ,payment.getCurrency());
            webhookService.sendWebhook(payload,payment);
        }

        //save wallet
        walletRepository.save(wallet);

        //updating payment status
        paymentRepository.save(payment);

        //save transaction if successful
        if(bankResponseStatus == BankResponseStatus.SUCCESS) {
            Transaction tx = new Transaction(payment, payment.getAmount(),TransactionStatus.SUCCESS , TransactionType.DEBIT);
            transactionRespository.save(tx);
        }

        return new PaymentCaptureResponseDTO(paymentId, payment.getStatus().toString(), wallet.getBalance());

    }

}
