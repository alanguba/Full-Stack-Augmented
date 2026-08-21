package com.api.agb.itera.filter;

import com.api.agb.itera.auth.OtpAuthentication;
import com.api.agb.itera.auth.UsernamePasswordAuthentication;
import com.api.agb.itera.model.Rol;
import com.api.agb.itera.model.Usuario;
import com.api.agb.itera.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class InitialAuthenticationFilterTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UsuarioRepository usuarioRepository;

    private InitialAuthenticationFilter filter;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);

        filter = new InitialAuthenticationFilter(
                authenticationManager,
                usuarioRepository
        );

        ReflectionTestUtils.setField(
                filter,
                "signingKey",
                "MyVerySecretJwtSigningKeyThatMustBeLongEnough123456789"
        );

        ReflectionTestUtils.setField(
                filter,
                "jwtExpirationMs",
                3600000L
        );
    }

    @Test
    void shouldNotFilter_returnsFalseForLoginPath() {

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.setServletPath("/login");

        assertFalse(
                filter.shouldNotFilter(request)
        );
    }

    @Test
    void shouldNotFilter_returnsTrueForNonLoginPath() {

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.setServletPath("/usuarios");

        assertTrue(
                filter.shouldNotFilter(request)
        );
    }

    @Test
    void doFilterInternal_authenticatesUsernamePassword()
            throws Exception {

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.addHeader(
                "username",
                "alan@email.com"
        );

        request.addHeader(
                "password",
                "password123"
        );

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        MockFilterChain filterChain =
                new MockFilterChain();

        Authentication authentication =
                mock(Authentication.class);

        when(authenticationManager.authenticate(any()))
                .thenReturn(authentication);

        filter.doFilterInternal(
                request,
                response,
                filterChain
        );

        verify(authenticationManager)
                .authenticate(
                        any(
                                UsernamePasswordAuthentication.class
                        )
                );

        assertNull(
                response.getHeader("Authorization")
        );
    }

    @Test
    void doFilterInternal_generatesJwtWhenOtpProvided()
            throws Exception {

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.addHeader(
                "username",
                "alan@email.com"
        );

        request.addHeader(
                "code",
                "123456"
        );

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        MockFilterChain filterChain =
                new MockFilterChain();

        Rol rol = new Rol();
        rol.setId(1L);
        rol.setNombre("ADMINISTRADOR");
        rol.setDescripcion("Administrador");

        Usuario usuario = new Usuario();
        usuario.setId(10L);
        usuario.setCorreo("alan@email.com");
        usuario.setRol(rol);

        Authentication authentication =
                mock(Authentication.class);

        when(authenticationManager.authenticate(any()))
                .thenReturn(authentication);

        when(usuarioRepository.findByCorreoIgnoreCase(
                "alan@email.com"
        )).thenReturn(Optional.of(usuario));

        filter.doFilterInternal(
                request,
                response,
                filterChain
        );

        verify(authenticationManager)
                .authenticate(
                        any(OtpAuthentication.class)
                );

        String jwt =
                response.getHeader("Authorization");

        assertNotNull(jwt);
        assertFalse(jwt.isBlank());
    }

    @Test
    void doFilterInternal_throwsExceptionWhenUserNotFoundInOtpFlow() {

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.addHeader(
                "username",
                "alan@email.com"
        );

        request.addHeader(
                "code",
                "123456"
        );

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        MockFilterChain filterChain =
                new MockFilterChain();

        Authentication authentication =
                mock(Authentication.class);

        when(authenticationManager.authenticate(any()))
                .thenReturn(authentication);

        when(usuarioRepository.findByCorreoIgnoreCase(
                "alan@email.com"
        )).thenReturn(Optional.empty());

        assertThrows(
                NullPointerException.class,
                () -> filter.doFilterInternal(
                        request,
                        response,
                        filterChain
                )
        );
    }
}