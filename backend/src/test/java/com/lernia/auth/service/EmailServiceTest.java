package com.lernia.auth.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

class EmailServiceTest {

    private JavaMailSender mailSender;
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        mailSender = mock(JavaMailSender.class);
        emailService = new EmailService();
        var field = EmailService.class.getDeclaredFields()[0];
        field.setAccessible(true);
        try {
            field.set(emailService, mailSender);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void sendPasswordResetEmail_sendsCorrectMessage() {
        String to = "user@example.com";
        String link = "http://localhost/reset?token=abc";

        emailService.sendPasswordResetEmail(to, link);

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());

        SimpleMailMessage msg = captor.getValue();
        assertThat(msg.getTo()).contains(to);
        assertThat(msg.getSubject()).isEqualTo("Reset your password");
        assertThat(msg.getText()).contains(link);
    }
}