package com.api.agb.itera.service;

import com.api.agb.itera.model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void sendOtpEmail_sendsExpectedEmail() {

        String email = "alan@example.com";
        String otpCode = "123456";

        emailService.sendOtpEmail(email, otpCode);

        ArgumentCaptor<SimpleMailMessage> captor =
                ArgumentCaptor.forClass(SimpleMailMessage.class);

        verify(mailSender, times(1))
                .send(captor.capture());

        SimpleMailMessage message = captor.getValue();

        assertEquals(email, message.getTo()[0]);
        assertEquals(
                "Your verification code",
                message.getSubject()
        );
        assertEquals(
                "Your OTP code is: 123456. It expires in 5 minutes.",
                message.getText()
        );
    }

    @Test
    void sendWelcomeEmail_sendsExpectedEmail() {

        Usuario usuario = new Usuario();
        usuario.setCorreo("alan@example.com");
        usuario.setNombre("Alan");

        emailService.sendWelcomeEmail(usuario);

        ArgumentCaptor<SimpleMailMessage> captor =
                ArgumentCaptor.forClass(SimpleMailMessage.class);

        verify(mailSender, times(1))
                .send(captor.capture());

        SimpleMailMessage message = captor.getValue();

        assertEquals(
                "alan@example.com",
                message.getTo()[0]
        );

        assertEquals(
                "Welcome to our service, Alan",
                message.getSubject()
        );

        assertEquals(
                "Thank you for registering with us! We're excited to have you on board. "
                        + "Remember to change your password to access the site. You just need to click on FORGOT MY PASSWORD. "
                        + "If you have any questions or need assistance, feel free to reach out to our support team.",
                message.getText()
        );
    }
}