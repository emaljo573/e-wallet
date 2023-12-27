package com.project.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.project.dto.UserReq;
import com.project.entity.User;
import com.project.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    @Autowired
    UserService userService;
    @PostMapping("/user")
    public void create(@RequestBody @Valid UserReq req) throws JsonProcessingException {
        userService.create(req.toUser());
    }

    @GetMapping("/user")
    public User getUser() throws Exception {
        User user= (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
       return userService.getUser(user.getId());
    }

    @GetMapping("/user/username/{phone}")
    public User getUserByPhone(@PathVariable("username") String  username) throws Exception {
        return (User) userService.loadUserByUsername(username);
    }
}
