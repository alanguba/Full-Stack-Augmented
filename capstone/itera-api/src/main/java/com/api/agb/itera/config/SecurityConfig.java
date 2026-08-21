package com.api.agb.itera.config;

import com.api.agb.itera.auth.providers.OtpAuthenticationProvider;
import com.api.agb.itera.auth.providers.UsernamePasswordAuthenticationProvider;
import com.api.agb.itera.filter.InitialAuthenticationFilter;
import com.api.agb.itera.filter.JwtAuthenticationFilter;
import com.api.agb.itera.service.UsuarioService;
import jakarta.servlet.DispatcherType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UsernamePasswordAuthenticationProvider usernamePasswordAuthenticationProvider(UsuarioService usuarioService ) {
        return new UsernamePasswordAuthenticationProvider(usuarioService);
    }

    @Bean
    public OtpAuthenticationProvider otpAuthenticationProvider(UsuarioService usuarioService ) {
        return new OtpAuthenticationProvider(usuarioService);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, InitialAuthenticationFilter initialAuthenticationFilter, JwtAuthenticationFilter jwtAuthenticationFilter, UsuarioService usuarioService) throws Exception {
        http
                .authenticationProvider(new UsernamePasswordAuthenticationProvider(usuarioService))
                .authenticationProvider(new OtpAuthenticationProvider(usuarioService))
                .addFilterAt(initialAuthenticationFilter, BasicAuthenticationFilter.class)
                .addFilterAfter(jwtAuthenticationFilter, BasicAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/api/usuario/**").hasRole("ADMINISTRADOR")
                        .requestMatchers("/error").permitAll()
                        .dispatcherTypeMatchers(DispatcherType.ERROR, DispatcherType.FORWARD).permitAll()
                        .anyRequest().authenticated()
                )
                .cors(cors-> {})
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationManagerBuilder authenticationManagerBuilder, AuthenticationConfiguration authenticationConfiguration, UsernamePasswordAuthenticationProvider usernamePasswordAuthenticationProvider, OtpAuthenticationProvider otpAuthenticationProvider) throws Exception {
        authenticationManagerBuilder.authenticationProvider(usernamePasswordAuthenticationProvider).authenticationProvider(otpAuthenticationProvider);

        return authenticationConfiguration.getAuthenticationManager();
    }
}
