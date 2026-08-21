package com.api.agb.itera.service;

import com.api.agb.itera.model.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendOtpEmail(String to, String otpCode) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Your verification code");
        message.setText("Your OTP code is: " + otpCode + ". It expires in 5 minutes.");
        mailSender.send(message);
    }

    public void sendWelcomeEmail(Usuario usuario){
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(usuario.getCorreo());
            message.setSubject("Welcome to our service, " + usuario.getNombre());
            message.setText("Thank you for registering with us! We're excited to have you on board. " +
                    "Remember to change your password to access the site. You just need to click on FORGOT MY PASSWORD. " +
                    "If you have any questions or need assistance, feel free to reach out to our support team.");
            mailSender.send(message);
    }
}