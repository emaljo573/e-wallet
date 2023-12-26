package com.project.repository;


import com.project.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletRepository  extends JpaRepository<Wallet,Integer> {
}
