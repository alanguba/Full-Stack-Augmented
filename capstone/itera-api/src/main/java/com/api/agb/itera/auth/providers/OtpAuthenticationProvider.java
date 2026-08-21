package com.api.agb.itera.auth.providers;

import com.api.agb.itera.auth.OtpAuthentication;
import com.api.agb.itera.dto.OtpValidateRequest;
import com.api.agb.itera.service.UsuarioService;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

public class OtpAuthenticationProvider implements AuthenticationProvider {
    private UsuarioService usuarioService;

    public OtpAuthenticationProvider(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String code = String.valueOf(authentication.getCredentials());
        OtpValidateRequest otpData = new OtpValidateRequest(username, code);
        boolean result = usuarioService.validateOtp(otpData);

        if(result){
            return new OtpAuthentication(username, code);
        } else{
            throw new BadCredentialsException("Bad credentials");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return OtpAuthentication.class.isAssignableFrom(authentication);
    }
}
