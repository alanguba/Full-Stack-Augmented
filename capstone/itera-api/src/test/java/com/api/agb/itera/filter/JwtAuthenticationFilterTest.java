package com.api.agb.itera.filter;

import com.api.agb.itera.auth.UsernamePasswordAuthentication;
import com.api.agb.itera.model.Rol;
import com.api.agb.itera.model.Usuario;
import com.api.agb.itera.repository.UsuarioRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class JwtAuthenticationFilterTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    private JwtAuthenticationFilter filter;

    private static final String SIGNING_KEY =
            "MyVerySecretJwtSigningKeyThatMustBeLongEnough123456789";

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);

        filter = new JwtAuthenticationFilter(
                usuarioRepository
        );

        ReflectionTestUtils.setField(
                filter,
                "signingKey",
                SIGNING_KEY
        );
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldNotFilter_returnsTrueForLogin() {

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.setServletPath("/login");

        assertTrue(filter.shouldNotFilter(request));
    }

    @Test
    void shouldNotFilter_returnsTrueForAuthEndpoints() {

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.setServletPath("/api/auth/reset-password");

        assertTrue(filter.shouldNotFilter(request));
    }

    @Test
    void shouldNotFilter_returnsTrueForH2Console() {

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.setServletPath("/h2-console/test");

        assertTrue(filter.shouldNotFilter(request));
    }

    @Test
    void shouldNotFilter_returnsTrueForErrorPath() {

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.setServletPath("/error");

        assertTrue(filter.shouldNotFilter(request));
    }

    @Test
    void shouldNotFilter_returnsFalseForProtectedPath() {

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.setServletPath("/api/usuario");

        assertFalse(filter.shouldNotFilter(request));
    }

    @Test
    void doFilterInternal_continuesWhenJwtMissing()
            throws Exception {

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        MockFilterChain chain =
                new MockFilterChain();

        filter.doFilterInternal(
                request,
                response,
                chain
        );

        assertNull(
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
        );
    }

    @Test
    void doFilterInternal_authenticatesValidJwt()
            throws Exception {

        String jwt = createValidJwt(
                "alan@email.com"
        );

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.addHeader(
                "Authorization",
                jwt
        );

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        MockFilterChain chain =
                new MockFilterChain();

        Rol rol = new Rol();
        rol.setId(1L);
        rol.setNombre("ADMINISTRADOR");
        rol.setDescripcion("Administrador");

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setCorreo("alan@email.com");
        usuario.setRol(rol);

        when(usuarioRepository.findByCorreoIgnoreCase(
                "alan@email.com"
        )).thenReturn(Optional.of(usuario));

        filter.doFilterInternal(
                request,
                response,
                chain
        );

        Authentication auth =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        assertNotNull(auth);

        assertEquals(
                "alan@email.com",
                auth.getName()
        );

        assertTrue(
                auth.getAuthorities()
                        .stream()
                        .anyMatch(a ->
                                a.getAuthority()
                                        .equals("ROLE_ADMINISTRADOR"))
        );
    }

    @Test
    void doFilterInternal_returnsUnauthorizedForInvalidJwt()
            throws Exception {

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.addHeader(
                "Authorization",
                "invalid-jwt"
        );

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        MockFilterChain chain =
                new MockFilterChain();

        filter.doFilterInternal(
                request,
                response,
                chain
        );

        assertEquals(
                401,
                response.getStatus()
        );

        assertEquals(
                "Invalid JWT",
                response.getContentAsString()
        );
    }

    @Test
    void doFilterInternal_throwsWhenUserNotFound()
            throws Exception {

        String jwt = createValidJwt(
                "alan@email.com"
        );

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.addHeader(
                "Authorization",
                jwt
        );

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        MockFilterChain chain =
                new MockFilterChain();

        when(usuarioRepository.findByCorreoIgnoreCase(
                "alan@email.com"
        )).thenReturn(Optional.empty());

        assertThrows(
                NullPointerException.class,
                () -> filter.doFilterInternal(
                        request,
                        response,
                        chain
                )
        );
    }

    private String createValidJwt(String username) {

        SecretKey key =
                Keys.hmacShaKeyFor(
                        SIGNING_KEY.getBytes(
                                StandardCharsets.UTF_8
                        )
                );

        return Jwts.builder()
                .claim("username", username)
                .setIssuedAt(new Date())
                .setExpiration(
                        new Date(
                                System.currentTimeMillis()
                                        + 60000
                        )
                )
                .signWith(key)
                .compact();
    }
}