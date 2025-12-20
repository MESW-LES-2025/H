package com.lernia.auth.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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

    @Test
    void sendPasswordResetEmail_callsMailSenderOnce() {
        emailService.sendPasswordResetEmail("user@example.com", "link");

        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "http://localhost/reset?token=123",
            "https://myapp.com/reset?token=xyz"
    })
    void sendPasswordResetEmail_includesResetLink(String link) {
        emailService.sendPasswordResetEmail("user@example.com", link);

        ArgumentCaptor<SimpleMailMessage> captor =
                ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());

        assertThat(captor.getValue().getText()).contains(link);
    }

    @Test
    void sendPasswordResetEmail_propagatesException() {
        doThrow(new MailSendException("SMTP error"))
                .when(mailSender)
                .send(any(SimpleMailMessage.class));

        assertThatThrownBy(() ->
                emailService.sendPasswordResetEmail("user@example.com", "link"))
                .isInstanceOf(MailSendException.class);
    }

    @Test
    void sendPasswordResetEmail_allowsNullEmail() {
        emailService.sendPasswordResetEmail(null, "link");

        verify(mailSender).send(any(SimpleMailMessage.class));
    }

}