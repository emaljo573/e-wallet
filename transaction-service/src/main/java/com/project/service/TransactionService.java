package com.project.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.dto.TransactionReq;
import com.project.entity.Transaction;
import com.project.entity.TransactionStatus;
import com.project.repository.TransactionRepository;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Service
public class TransactionService {

    @Autowired
    TransactionRepository repo;

    @Autowired
    KafkaTemplate<String,String> kafkaTemplate;

   private ObjectMapper objectMapper=new ObjectMapper();
   private RestTemplate restTemplate=new RestTemplate();


    private static final String TRANSACTION_CREATED="transaction_created";
    private static final String WALLET_UPDATED_TOPIC="wallet_updated";
    private static final String TRANSACTION_COMPLETED_TOPIC="transaction_completed";
    public String transact(TransactionReq transactionReq,String senderId) throws JsonProcessingException {
        Transaction transaction=Transaction.builder().
                senderId(senderId).
                receiverId(transactionReq.getReceiver()).
                reason(transactionReq.getComment()).
                amount(transactionReq.getAmount()).
                transactionId(UUID.randomUUID().toString()).
                transactionStatus(TransactionStatus.PENDING).
                build();
        repo.save(transaction);
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("senderId",transaction.getSenderId());
        jsonObject.put("receiverId",transaction.getReceiverId());
        jsonObject.put("amount",transaction.getAmount());
        jsonObject.put("transactionId",transaction.getTransactionId());
        kafkaTemplate.send(TRANSACTION_CREATED,objectMapper.writeValueAsString(jsonObject));
        return transaction.getTransactionId();
    }

    @KafkaListener(topics = {WALLET_UPDATED_TOPIC},groupId = "payment-app")
    public void updateTransaction(String msg) throws ParseException, JsonProcessingException {
        JSONObject obj= (JSONObject) new JSONParser().parse(msg);
        String transactionId= (String) obj.get("transactionId");
        String receiverId= (String) obj.get("receiverId");
        String senderId= (String) obj.get("senderId");
        String status= (String) obj.get("status");
        Long amount= (Long) obj.get("amount");
        if(status.equals("FAILED")){
            repo.updateTransaction(transactionId,TransactionStatus.FAILED);
        }else{
            repo.updateTransaction(transactionId,TransactionStatus.SUCCESS);
        }
        JSONObject notifObj=new JSONObject();
        notifObj.put("transactionId",transactionId);
        notifObj.put("status",status);
        notifObj.put("amount",amount);
        JSONObject senderObj=this.restTemplate.getForObject("http://localhost:9090/user/username/"+senderId, JSONObject.class);
        JSONObject receiverObj=this.restTemplate.getForObject("http://localhost:9090/user/username/"+receiverId, JSONObject.class);
        String senderEmail=senderObj==null?null:(String) senderObj.get("email");
        String receiverEmail=receiverObj==null?null:(String) receiverObj.get("email");
        notifObj.put("senderEmail",senderEmail);
        notifObj.put("receiverEmail",receiverEmail);
        notifObj.put("senderPhone",senderId);
        notifObj.put("receiverPhone",receiverId);
        kafkaTemplate.send(TRANSACTION_COMPLETED_TOPIC,objectMapper.writeValueAsString(notifObj));
    }
}
