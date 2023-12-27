package com.project.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.entity.User;
import com.project.repository.UserCacheRepository;
import com.project.repository.UserRepository;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {

    private static final String USER_CREATED_TOPIC="user_created";
    @Autowired
    UserRepository userRepository;

    @Autowired
    UserCacheRepository userCacheRepository;

    @Autowired
    KafkaTemplate<String,String> kafkaTemplate;

    @Autowired
    PasswordEncoder passwordEncoder;

    ObjectMapper objectMapper=new ObjectMapper();
    public void create(User user) throws JsonProcessingException {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setAuthorities("usr");
        userRepository.save(user);
        JSONObject userObj=new JSONObject();
        userObj.put("phone",user.getUsername());
        userObj.put("email",user.getEmail());
        kafkaTemplate.send(USER_CREATED_TOPIC,objectMapper.writeValueAsString(userObj));
    }

    public User getUser(Integer userId) throws Exception {
        User user=userCacheRepository.get(userId);
        if(user!=null){
            return user;
        }
       user= userRepository.findById(userId).orElseThrow(()->new Exception());
        userCacheRepository.set(user);
        return user;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username);
    }
}
