package com.api.agb.itera.service;

import com.api.agb.itera.dto.*;
import com.api.agb.itera.exception.CorreoDuplicadoException;
import com.api.agb.itera.model.Rol;
import com.api.agb.itera.model.Usuario;
import com.api.agb.itera.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private UsuarioService usuarioService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllUsers_returnsUsers() {

        Usuario usuario = createUsuario();

        when(usuarioRepository.findAll())
                .thenReturn(List.of(usuario));

        List<UsuarioDto> result =
                usuarioService.getAllUsers();

        assertEquals(1, result.size());
        assertEquals("alan@email.com", result.get(0).correo());

        verify(usuarioRepository).findAll();
    }

    @Test
    void getAllUsers_returnsEmptyList() {

        when(usuarioRepository.findAll())
                .thenReturn(List.of());

        List<UsuarioDto> result =
                usuarioService.getAllUsers();

        assertTrue(result.isEmpty());
    }

    @Test
    void addUsuario_createsUser() {

        UsuarioAddRequest request = createAddRequest();

        Usuario savedUsuario = createUsuario();
        savedUsuario.setId(1L);

        when(usuarioRepository.existsByCorreoIgnoreCase(
                request.correo()))
                .thenReturn(false);

        when(passwordEncoder.encode(anyString()))
                .thenReturn("encoded-password");

        when(usuarioRepository.save(any(Usuario.class)))
                .thenReturn(savedUsuario);

        UsuarioAddResponse response =
                usuarioService.addUsuario(request);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("alan@email.com", response.correo());

        verify(usuarioRepository)
                .save(any(Usuario.class));

        verify(emailService)
                .sendWelcomeEmail(any(Usuario.class));
    }

    @Test
    void addUsuario_throwsExceptionWhenEmailExists() {

        UsuarioAddRequest request = createAddRequest();

        when(usuarioRepository.existsByCorreoIgnoreCase(
                request.correo()))
                .thenReturn(true);

        assertThrows(
                CorreoDuplicadoException.class,
                () -> usuarioService.addUsuario(request)
        );

        verify(usuarioRepository, never())
                .save(any());
    }

    @Test
    void updateUsuario_updatesUser() {

        Usuario usuario = createUsuario();

        UsuarioEditRequest request =
                createEditRequest();

        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuario));

        when(usuarioRepository.existsByCorreoAndIdNot(
                request.correo(),
                request.id()))
                .thenReturn(false);

        when(usuarioRepository.save(any(Usuario.class)))
                .thenReturn(usuario);

        UsuarioAddResponse response =
                usuarioService.updateUsuario(request);

        assertEquals(
                request.correo(),
                response.correo()
        );

        verify(usuarioRepository)
                .save(usuario);
    }

    @Test
    void updateUsuario_throwsExceptionWhenUserNotFound() {

        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                EntityNotFoundException.class,
                () -> usuarioService.updateUsuario(
                        createEditRequest()
                )
        );
    }

    @Test
    void updateUsuario_throwsExceptionWhenEmailAlreadyExists() {

        Usuario usuario = createUsuario();

        UsuarioEditRequest request =
                createEditRequest();

        when(usuarioRepository.findById(request.id()))
                .thenReturn(Optional.of(usuario));

        when(usuarioRepository.existsByCorreoAndIdNot(
                request.correo(),
                request.id()))
                .thenReturn(true);

        assertThrows(
                IllegalArgumentException.class,
                () -> usuarioService.updateUsuario(request)
        );
    }

    @Test
    void updateUsuarioStatus_updatesStatus() {

        Usuario usuario = createUsuario();

        UsuarioEditRequest request =
                createEditRequest();

        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuario));

        when(usuarioRepository.save(any(Usuario.class)))
                .thenReturn(usuario);

        UsuarioAddResponse response =
                usuarioService.updateUsuarioStatus(request);

        assertEquals(
                request.estado(),
                response.estado()
        );
    }

    @Test
    void updateUsuarioStatus_throwsExceptionWhenUserNotFound() {

        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                EntityNotFoundException.class,
                () -> usuarioService.updateUsuarioStatus(
                        createEditRequest()
                )
        );
    }

    @Test
    void auth_generatesOtpAndSendsEmail() {

        Usuario usuario = createUsuario();
        usuario.setContrasena("encoded-password");

        LoginRequest request =
                new LoginRequest(
                        "alan@email.com",
                        "password"
                );

        when(usuarioRepository.findByCorreoIgnoreCase(
                request.correo()))
                .thenReturn(Optional.of(usuario));

        when(passwordEncoder.matches(
                "password",
                "encoded-password"))
                .thenReturn(true);

        usuarioService.auth(request);

        verify(usuarioRepository)
                .save(usuario);

        verify(emailService)
                .sendOtpEmail(
                        eq(usuario.getCorreo()),
                        any(String.class)
                );

        assertNotNull(usuario.getOtp());
    }

    @Test
    void auth_throwsExceptionWhenPasswordIsInvalid() {

        Usuario usuario = createUsuario();

        when(usuarioRepository.findByCorreoIgnoreCase(
                "alan@email.com"))
                .thenReturn(Optional.of(usuario));

        when(passwordEncoder.matches(
                anyString(),
                anyString()))
                .thenReturn(false);

        assertThrows(
                BadCredentialsException.class,
                () -> usuarioService.auth(
                        new LoginRequest(
                                "alan@email.com",
                                "wrong-password"
                        )
                )
        );
    }

    @Test
    void auth_throwsExceptionWhenUserDoesNotExist() {

        when(usuarioRepository.findByCorreoIgnoreCase(
                "alan@email.com"))
                .thenReturn(Optional.empty());

        assertThrows(
                BadCredentialsException.class,
                () -> usuarioService.auth(
                        new LoginRequest(
                                "alan@email.com",
                                "password"
                        )
                )
        );
    }

    @Test
    void validateOtp_returnsTrue() {

        Usuario usuario = createUsuario();
        usuario.setOtp("123456");

        when(usuarioRepository.findByCorreoIgnoreCase(
                "alan@email.com"))
                .thenReturn(Optional.of(usuario));

        boolean result =
                usuarioService.validateOtp(
                        new OtpValidateRequest(
                                "alan@email.com",
                                "123456"
                        )
                );

        assertTrue(result);
    }

    @Test
    void validateOtp_returnsFalse() {

        Usuario usuario = createUsuario();
        usuario.setOtp("123456");

        when(usuarioRepository.findByCorreoIgnoreCase(
                "alan@email.com"))
                .thenReturn(Optional.of(usuario));

        boolean result =
                usuarioService.validateOtp(
                        new OtpValidateRequest(
                                "alan@email.com",
                                "999999"
                        )
                );

        assertFalse(result);
    }

    @Test
    void validateOtp_returnsFalseWhenUserDoesNotExist() {

        when(usuarioRepository.findByCorreoIgnoreCase(
                "alan@email.com"))
                .thenReturn(Optional.empty());

        boolean result =
                usuarioService.validateOtp(
                        new OtpValidateRequest(
                                "alan@email.com",
                                "123456"
                        )
                );

        assertFalse(result);
    }

    private Usuario createUsuario() {

        Rol rol = new Rol();
        rol.setId(1L);
        rol.setNombre("USUARIO");
        rol.setDescripcion("Rol Usuario");

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setCorreo("alan@email.com");
        usuario.setNombre("Alan");
        usuario.setApellidoPaterno("Gutierrez");
        usuario.setApellidoMaterno("Banuelos");
        usuario.setEstado((short) 1);
        usuario.setRol(rol);
        usuario.setContrasena("encoded-password");

        return usuario;
    }

    private UsuarioAddRequest createAddRequest() {

        RolDto rolDto = new RolDto(
                1L,
                "USUARIO",
                "Rol Usuario"
        );

        return new UsuarioAddRequest(
                "alan@email.com",
                "Alan",
                "Gutierrez",
                "Banuelos",
                rolDto
        );
    }

    private UsuarioEditRequest createEditRequest() {

        Rol rol = new Rol();
        rol.setId(1L);
        rol.setNombre("USUARIO");
        rol.setDescripcion("Rol Usuario");

        return new UsuarioEditRequest(
                1L,
                "alan@email.com",
                "Alan Updated",
                "Gutierrez",
                "Banuelos",
                (short) 1,
                rol
        );
    }
}