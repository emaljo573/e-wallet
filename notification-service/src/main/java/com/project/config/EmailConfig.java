package com.project.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class EmailConfig {

    @Bean
    SimpleMailMessage getMailMessage(){
        return new SimpleMailMessage();
    }
    @Bean
    JavaMailSender getMailSender(){
        JavaMailSenderImpl javaMailSender=new JavaMailSenderImpl();
        javaMailSender.setHost("smtp.gmail.com");
        javaMailSender.setPort(587);
        javaMailSender.setUsername("belljarofsylvia@gmail.com");
        javaMailSender.setPassword("rucq xzpi isia odqz");
        Properties properties=javaMailSender.getJavaMailProperties();
        properties.put("mail.debug",true);
        properties.put("mail.smtp.starttls.enable",true);
        return javaMailSender;
    }
}
