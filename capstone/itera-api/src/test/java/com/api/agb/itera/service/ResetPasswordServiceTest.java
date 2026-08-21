package com.api.agb.itera.service;

import com.api.agb.itera.dto.ResetPasswordRequest;
import com.api.agb.itera.model.Usuario;
import com.api.agb.itera.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ResetPasswordServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private ResetPasswordService resetPasswordService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void resetPassword_throwsExceptionWhenUserDoesNotExist() {

        ResetPasswordRequest request =
                new ResetPasswordRequest(
                        "test@example.com",
                        null,
                        null
                );

        when(usuarioRepository.findByCorreoIgnoreCase(
                "test@example.com"))
                .thenReturn(Optional.empty());

        BadCredentialsException exception =
                assertThrows(
                        BadCredentialsException.class,
                        () -> resetPasswordService.resetPassword(request)
                );

        assertEquals(
                "Bad credentials",
                exception.getMessage()
        );

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void resetPassword_generatesOtpWhenOtpIsMissing() {

        Usuario usuario = new Usuario();
        usuario.setCorreo("test@example.com");

        ResetPasswordRequest request =
                new ResetPasswordRequest(
                        "test@example.com",
                        null,
                        null
                );

        when(usuarioRepository.findByCorreoIgnoreCase(
                "test@example.com"))
                .thenReturn(Optional.of(usuario));

        when(usuarioRepository.save(any(Usuario.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        resetPasswordService.resetPassword(request);

        verify(usuarioRepository).save(usuario);

        verify(emailService)
                .sendOtpEmail(
                        eq("test@example.com"),
                        any(String.class)
                );

        assertNotNull(usuario.getOtp());
    }

    @Test
    void resetPassword_updatesPasswordWhenOtpIsValid() {

        Usuario usuario = new Usuario();
        usuario.setCorreo("test@example.com");
        usuario.setOtp("123456");

        ResetPasswordRequest request =
                new ResetPasswordRequest(
                        "test@example.com",
                        "123456",
                        "NewPassword123"
                );

        when(usuarioRepository.findByCorreoIgnoreCase(
                "test@example.com"))
                .thenReturn(Optional.of(usuario));

        when(passwordEncoder.encode("NewPassword123"))
                .thenReturn("encoded-password");

        when(usuarioRepository.save(any(Usuario.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        resetPasswordService.resetPassword(request);

        assertEquals(
                "encoded-password",
                usuario.getContrasena()
        );

        assertNull(usuario.getOtp());

        verify(usuarioRepository).save(usuario);
        verify(emailService, never())
                .sendOtpEmail(any(), any());
    }

    @Test
    void resetPassword_throwsExceptionWhenOtpIsInvalid() {

        Usuario usuario = new Usuario();
        usuario.setCorreo("test@example.com");
        usuario.setOtp("123456");

        ResetPasswordRequest request =
                new ResetPasswordRequest(
                        "test@example.com",
                        "999999",
                        "NewPassword123"
                );

        when(usuarioRepository.findByCorreoIgnoreCase(
                "test@example.com"))
                .thenReturn(Optional.of(usuario));

        BadCredentialsException exception =
                assertThrows(
                        BadCredentialsException.class,
                        () -> resetPasswordService.resetPassword(request)
                );

        assertEquals(
                "Invalid OTP",
                exception.getMessage()
        );

        verify(usuarioRepository, never()).save(any());
        verify(emailService, never())
                .sendOtpEmail(any(), any());
    }

    @Test
    void resetPassword_generatesOtpWhenOtpIsEmptyString() {

        Usuario usuario = new Usuario();
        usuario.setCorreo("test@example.com");

        ResetPasswordRequest request =
                new ResetPasswordRequest(
                        "test@example.com",
                        "",
                        null
                );

        when(usuarioRepository.findByCorreoIgnoreCase(
                "test@example.com"))
                .thenReturn(Optional.of(usuario));

        resetPasswordService.resetPassword(request);

        verify(usuarioRepository).save(usuario);

        verify(emailService)
                .sendOtpEmail(
                        eq("test@example.com"),
                        any(String.class)
                );

        assertNotNull(usuario.getOtp());
    }
}