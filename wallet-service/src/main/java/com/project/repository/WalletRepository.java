package com.project.repository;


import com.project.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface WalletRepository  extends JpaRepository<Wallet,Integer> {

    Wallet findByWalletId(String walletId);

    @Transactional
    @Modifying
    @Query("update Wallet w set w.balance=w.balance+ :amount where w.walletId= :walletId ")
    void updateWallet(String walletId,Long amount);
}
