package com.project.service;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    @Autowired
    SimpleMailMessage simpleMailMessage;

    @Autowired
    JavaMailSender javaMailSender;
    private static final String TRANSACTION_COMPLETED_TOPIC="transaction_completed";

    @KafkaListener(topics = {TRANSACTION_COMPLETED_TOPIC},groupId = "payment-app")
    public void notify(String msg) throws ParseException {
        JSONObject obj= (JSONObject) new JSONParser().parse(msg);
        String status= (String) obj.get("status");
        String transactionId= (String) obj.get("transactionId");
        String senderEmail= (String) obj.get("senderEmail");
        String receiverEmail= (String) obj.get("receiverEmail");
        Long amount=(Long) obj.get("amount")/100;
        String senderMsg=getSenderMessage(status,transactionId,amount);
        String receiverMsg=getReceiverMessage(status,transactionId,amount,senderEmail);
        if(senderMsg!=null && senderMsg.length()>0){
            simpleMailMessage.setTo(senderEmail);
            simpleMailMessage.setSubject("E-Wallet Transaction Updates");
            simpleMailMessage.setFrom("belljarofsylvia@gmail.com");
            simpleMailMessage.setText(senderMsg);
            javaMailSender.send(simpleMailMessage);
        }
        if(receiverMsg!=null && receiverMsg.length()>0){
            simpleMailMessage.setTo(receiverEmail);
            simpleMailMessage.setSubject("E-Wallet Transaction Updates");
            simpleMailMessage.setFrom("belljarofsylvia@gmail.com");
            simpleMailMessage.setText(receiverMsg);
            javaMailSender.send(simpleMailMessage);
        }
    }

    private String getSenderMessage(String status,String transactionId,Long amount){
        String msg="";
        if(status.equals("FAILED")){
            msg="Hi, your transaction: "+transactionId+" of amount"+amount+" has failed";
        }else{
            msg="Hi, your account has been debited with amount "+amount+" , transaction id= "+transactionId;
        }
        return msg;
    }
    private String getReceiverMessage(String status,String transactionId,Long amount,String senderEmail){
        String msg="";
        if(status.equals("SUCCESS")){
            msg="Hi, your account has been credited with amount "+amount+ "from the transaction done by "+senderEmail+" , transaction id= "+transactionId;
        }
        return msg;
    }
}
