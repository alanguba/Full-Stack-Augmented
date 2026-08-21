package com.api.agb.itera.auth.providers;

import com.api.agb.itera.auth.UsernamePasswordAuthentication;
import com.api.agb.itera.dto.LoginRequest;
import com.api.agb.itera.service.UsuarioService;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

public class UsernamePasswordAuthenticationProvider implements AuthenticationProvider {

    private UsuarioService usuarioService;

    public UsernamePasswordAuthenticationProvider(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = String.valueOf(authentication.getCredentials());

        LoginRequest usuarioData = new LoginRequest(username, password);

        usuarioService.auth(usuarioData);
        return new UsernamePasswordAuthenticationToken(username, password);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthentication.class.isAssignableFrom(authentication);
    }
}
