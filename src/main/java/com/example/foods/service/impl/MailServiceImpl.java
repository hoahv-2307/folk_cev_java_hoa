package com.example.foods.service.impl;

import com.example.foods.service.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

  private final JavaMailSender mailSender;

  @Value("${spring.mail.username}")
  private final String adminEmail;

  @Override
  @Async
  public void sendSimpleMail(String subject, String body) {
    log.info("Sending email to admin: {}", adminEmail);
    try {
      SimpleMailMessage message = new SimpleMailMessage();
      message.setFrom(adminEmail);
      message.setTo(adminEmail);
      message.setSubject(subject);
      message.setText(body);
      mailSender.send(message);
    } catch (Exception e) {
      log.error("Failed to send email: {}", e.getMessage());
    }
  }
}
