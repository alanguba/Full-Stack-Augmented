package com.api.agb.itera.service;

import com.api.agb.itera.dto.ResetPasswordRequest;
import com.api.agb.itera.model.Usuario;
import com.api.agb.itera.repository.UsuarioRepository;
import com.api.agb.itera.utils.GenerateCodeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class ResetPasswordService {
    final private UsuarioRepository usuarioRepository;
    final private PasswordEncoder passwordEncoder;
    final private EmailService emailService;

    @Autowired
    public ResetPasswordService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, EmailService emailService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    public void resetPassword(ResetPasswordRequest resetPasswordRequest) {
        Optional<Usuario> usuario = usuarioRepository.findByCorreoIgnoreCase(resetPasswordRequest.email());
        log.info("Correo: {}", resetPasswordRequest.email());
        if (usuario.isPresent()) {
            Usuario u = usuario.get();
            String code = resetPasswordRequest.otp();
            log.info("OTP: {}", code);
            if(code == null || code.isEmpty()){
                code = GenerateCodeUtil.generateCode();
                u.setOtp(code);
                log.info("Generated OTP: {}", code);
                usuarioRepository.save(u);
                emailService.sendOtpEmail(u.getCorreo(), code);
            } else if (code.equals(u.getOtp())) {
                String newPassword = resetPasswordRequest.newPassword();
                u.setContrasena(passwordEncoder.encode(newPassword));
                u.setOtp(null); // Limpiar el OTP después de usarlo
                usuarioRepository.save(u);
            } else {
                throw new BadCredentialsException("Invalid OTP");
            }

        } else {
            throw new BadCredentialsException("Bad credentials");
        }
    }
}
