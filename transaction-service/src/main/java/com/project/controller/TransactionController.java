package com.project.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.project.dto.TransactionReq;
import com.project.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TransactionController {

    @Autowired
    TransactionService transactionService;
    @PostMapping("/transaction")
    public String transaction(@RequestBody @Valid TransactionReq req) throws JsonProcessingException {
        return transactionService.transact(req);
    }
}
