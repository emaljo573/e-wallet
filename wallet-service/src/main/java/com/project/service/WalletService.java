package com.project.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.entity.Wallet;
import com.project.repository.WalletRepository;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class WalletService {

    @Autowired
    WalletRepository walletRepository;

    private static final String USER_CREATION_TOPIC ="user_created";
    private static final String TRANSACTION_CREATED_TOPIC="transaction_created";
    private static final String WALLET_UPDATED_TOPIC="wallet_updated";

    private ObjectMapper objMapper=new ObjectMapper();
    @Autowired
    KafkaTemplate<String,String> kafkaTemplate;

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

    @KafkaListener(topics = {TRANSACTION_CREATED_TOPIC},groupId = "payment-app")
    public void updateWallets(String msg) throws JsonProcessingException, ParseException {

        JSONObject obj = (JSONObject) new JSONParser().parse(msg);
        String receiverId = (String) obj.get("receiverId");
        String senderId = (String) obj.get("senderId");
        String transactionId = (String) obj.get("transactionId");
        Long amount = (Long) obj.get("amount");
        try {
            Wallet senderWallet = walletRepository.findByWalletId(senderId);
            Wallet receiverWallet = walletRepository.findByWalletId(receiverId);
            if (senderWallet == null || receiverWallet == null || senderWallet.getBalance() < amount) {
                JSONObject jsonObj = createObject(transactionId,senderId,receiverId,"FAILED",amount);
                jsonObj.put("senderBalance", senderWallet.getBalance());
                kafkaTemplate.send(WALLET_UPDATED_TOPIC, objMapper.writeValueAsString(jsonObj));
                return;
            }
            walletRepository.updateWallet(senderId, 0 - amount);
            walletRepository.updateWallet(receiverId, amount);
            JSONObject jsonObj = createObject(transactionId,senderId,receiverId,"SUCCESS",amount);
            kafkaTemplate.send(WALLET_UPDATED_TOPIC, objMapper.writeValueAsString(jsonObj));

        }catch (Exception e){
            JSONObject jsonObj = createObject(transactionId,senderId,receiverId,"FAILED",amount);
            jsonObj.put("error",e.getMessage());
            kafkaTemplate.send(WALLET_UPDATED_TOPIC, objMapper.writeValueAsString(jsonObj));
            return;

        }
    }

    private JSONObject createObject(String txnId,String senderId,String receiverId,String status,Long amount){
        JSONObject obj=new JSONObject();
        obj.put("transactionId", txnId);
        obj.put("senderId", senderId);
        obj.put("receiverId", receiverId);
        obj.put("status", status);
        obj.put("amount",amount);
        return obj;
    }
}
