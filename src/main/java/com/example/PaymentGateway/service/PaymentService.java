package com.example.PaymentGateway.service;

import com.example.PaymentGateway.dto.PaymentRequestDTO;
import com.example.PaymentGateway.dto.PaymentResponseDTO;
import com.example.PaymentGateway.enums.PaymentStatus;
import com.example.PaymentGateway.exception.InvalidRequestException;
import com.example.PaymentGateway.exception.ResourceNotFoundException;
import com.example.PaymentGateway.model.IdempotencyKey;
import com.example.PaymentGateway.model.Payment;
import com.example.PaymentGateway.model.User;
import com.example.PaymentGateway.repository.IdempotencyRepository;
import com.example.PaymentGateway.repository.PaymentRepository;
import com.example.PaymentGateway.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;


import java.math.BigDecimal;


@Service
@Transactional
public class PaymentService {


    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private IdempotencyRepository idempotencyRepository;

    @Autowired
    private Hashing hashing;


    // here I have to perform
    // 1. check for double payment using idempotency key
    // 2. validate user exists
    // 3. check is payment is possible
    // 4. save payment
    // 5. return dto as a respoance
    // 6. set status and save idempotency key

    public PaymentResponseDTO createPayment(@Valid PaymentRequestDTO request, String key) {
        Long userId = request.getUserId();
        String description = request.getDescription();
        BigDecimal amount = request.getAmount();
        String currency = request.getCurrency();
        String requestedHash = hashing.hash(request);

        // check key
        if(key == null || key.isBlank()) throw new InvalidRequestException("invalid key");

        // check payment is already exist or not
        IdempotencyKey record = idempotencyRepository.findByIdempotencyKey(key).orElse(null);


        // record is not null means payment already exist
        if (record != null) {

            // getting hash code of current request from idempotency table in database
            String currentHash = record.getRequestHash();

            //checking hash code of current request and previous payment request with same idempotency Key
            if(!currentHash.equals(requestedHash)) // then malasis activity is occuring
                throw  new InvalidRequestException("some malasis Activity is happening -- Bad Request");

            // means both hash code are same so we can return old payemnt as a respoance
            Long paymentId = Long.parseLong(record.getResponseBody());

            Payment existingPayment = paymentRepository
                    .findById(paymentId)
                    .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));

            // creating(using pre defined cunstructor )  and returning paymentRespoanceDTO
            return new PaymentResponseDTO(
                    existingPayment.getUser().getId(),
                    existingPayment.getAmount(),
                    existingPayment.getStatus().toString(),
                    existingPayment.getCreatedAt()
            );
        }


        // check if user exists or not
        User user = userRepository.findById(userId).orElseThrow(()-> new ResourceNotFoundException("User not found"));

        // here we are just checking amount is valid or not
        if(amount.compareTo(BigDecimal.ZERO) <=0 ) throw new InvalidRequestException("Amount must be positive");
        // amount.compareTo(BigDecimal.ZERO)  return 0 if both values are equal , -1 if amount is less than value , 1 if amount is greater than value here if we get -1 or 0 means amount is less than 0


        // creating new payment
        Payment payment = new Payment();
        payment.setUser(user);
        payment.setAmount(amount);
        payment.setCurrency(currency);
        payment.setIdempotencyKey(key);
        payment.setDescription(description);
        payment.setStatus(PaymentStatus.CREATED);
        Payment newpayment = paymentRepository.save(payment);



        // saving new  idempotency record in idempotency record table
        IdempotencyKey keyobj = new IdempotencyKey();
        keyobj.setIdempotencyKey(key);
        keyobj.setResponseBody(newpayment.getId().toString());
        keyobj.setRequestHash(requestedHash);
        idempotencyRepository.save(keyobj);


        // creating and returnin RespoanceDTO
        return new PaymentResponseDTO(newpayment.getUser().getId(), newpayment.getAmount(), newpayment.getStatus().toString(), newpayment.getCreatedAt());
    }


}// ending of service class




