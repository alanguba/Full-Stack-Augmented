package com.api.agb.itera.filter;

import com.api.agb.itera.model.Usuario;
import com.api.agb.itera.repository.UsuarioRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.api.agb.itera.auth.UsernamePasswordAuthentication;
import com.api.agb.itera.auth.OtpAuthentication;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Component
public class InitialAuthenticationFilter extends OncePerRequestFilter {
    private AuthenticationManager authenticationManager;

    @Value("${jwt.signing.key}")
    private String signingKey;

    @Value("${jwt.expiration.ms}")
    private long jwtExpirationMs;

    private UsuarioRepository usuarioRepository;

    @Autowired
    public InitialAuthenticationFilter(AuthenticationManager authenticationManager, UsuarioRepository usuarioRepository) {
        this.authenticationManager = authenticationManager;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        String username = request.getHeader("username");
        String password = request.getHeader("password");
        String code = request.getHeader("code");

        if(code == null){
            Authentication a = new UsernamePasswordAuthentication(username, password);
            authenticationManager.authenticate(a);
        } else {
            Authentication a = new OtpAuthentication(username, code);
            a = authenticationManager.authenticate(a);

            SecretKey key = Keys.hmacShaKeyFor(signingKey.getBytes(StandardCharsets.UTF_8));

            Optional<Usuario> usuarioOpt = usuarioRepository.findByCorreoIgnoreCase(username);

            GrantedAuthority authority = null;

            if(usuarioOpt.isPresent()){
                Usuario usu = usuarioOpt.get();
                authority = new SimpleGrantedAuthority("ROLE_"+ usu.getRol().getNombre());
            }

            String jwt = Jwts.builder()
                    .setClaims(Map.of("id", usuarioOpt.map(Usuario::getId).orElse(null), "username", username, "role", authority.getAuthority()))
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                    .signWith(key)
                    .compact();

            response.setHeader("Authorization", jwt);
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request){
        return !request.getServletPath().equals("/login");
    }
}

