package com.api.agb.itera.filter;

import com.api.agb.itera.auth.UsernamePasswordAuthentication;
import com.api.agb.itera.model.Usuario;
import com.api.agb.itera.repository.UsuarioRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Value("${jwt.signing.key}")
    private String signingKey;

    private UsuarioRepository usuarioRepository;

    @Autowired
    public JwtAuthenticationFilter(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String jwt = request.getHeader("Authorization");
        List<GrantedAuthority> roles = List.of();

        if(jwt == null){
            filterChain.doFilter(request, response);
            return;
        }

        try{
            SecretKey key = Keys.hmacShaKeyFor(signingKey.getBytes(StandardCharsets.UTF_8));

            Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(jwt).getBody();

            String username = String.valueOf(claims.get("username"));

            Optional<Usuario> usuarioOpt = usuarioRepository.findByCorreoIgnoreCase(username);

            GrantedAuthority authority = null;

            if(usuarioOpt.isPresent()){
                Usuario usu = usuarioOpt.get();
                authority = new SimpleGrantedAuthority("ROLE_"+ usu.getRol().getNombre());
            }

            UsernamePasswordAuthentication auth = new UsernamePasswordAuthentication(username, null, List.of(authority));

            SecurityContextHolder.getContext().setAuthentication(auth);
        } catch (ExpiredJwtException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("JWT expired");
            return;
        } catch (JwtException | IllegalArgumentException e){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid JWT");
            return;
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.equals("/login")
                || path.startsWith("/h2-console")
                || path.startsWith("/api/auth")
                || path.startsWith("/error");
    }
}
