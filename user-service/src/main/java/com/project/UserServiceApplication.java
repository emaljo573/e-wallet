package com.project;

import com.project.entity.User;
import com.project.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class UserServiceApplication implements CommandLineRunner {

    @Autowired
    UserRepository userRepository;
    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class);
    }

    @Override
    public void run(String... args) throws Exception{
//        userRepository.save(User.builder().
//                username("txn-service").
//                password(new BCryptPasswordEncoder().encode("test123")).
//                authorities("svc").
//                build());
    }
}