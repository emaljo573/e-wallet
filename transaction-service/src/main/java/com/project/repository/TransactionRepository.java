package com.project.repository;

import com.project.entity.Transaction;
import com.project.entity.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface TransactionRepository extends JpaRepository<Transaction,Integer> {

    @Transactional
    @Modifying
    @Query("update Transaction t set t.transactionStatus= :status where t.transactionId= :transactionId")
    void updateTransaction(String transactionId, TransactionStatus status);
}
