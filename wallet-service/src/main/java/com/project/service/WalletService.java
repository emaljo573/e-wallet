package com.project.service;

import com.project.entity.Wallet;
import com.project.repository.WalletRepository;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class WalletService {

    @Autowired
    WalletRepository walletRepository;

    private static final String USER_CREATION_TOPIC ="user_created";

    @KafkaListener(topics = {USER_CREATION_TOPIC},groupId="payment-app")
    private void createWallet(String msg) throws ParseException {
        JSONObject obj= (JSONObject) new JSONParser().parse(msg);
        String walletId= (String) obj.get("phone");
        Wallet wallet=Wallet.builder().
                walletId(walletId).
                currency("INR").
                balance(0L).
                build();
        walletRepository.save(wallet);
    }
}
