package com.example.PaymentGateway.repository;

import com.example.PaymentGateway.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRespository extends JpaRepository<Transaction,Long> {
}
