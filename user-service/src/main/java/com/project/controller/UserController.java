package com.project.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.project.dto.UserReq;
import com.project.entity.User;
import com.project.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    @Autowired
    UserService userService;
    @PostMapping("/user")
    public void create(@RequestBody @Valid UserReq req) throws JsonProcessingException {
        userService.create(req.toUser());
    }

    @GetMapping("/user/{userId}")
    public User getUser(@PathVariable("userId") Integer userId) throws Exception {
       return userService.getUser(userId);
    }

    @GetMapping("/user/phone/{phone}")
    public User getUserByPhone(@PathVariable("phone") String  phone) throws Exception {
        return userService.getUserByPhone(phone);
    }
}
