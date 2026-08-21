package com.api.agb.itera.service;

import com.api.agb.itera.dto.*;
import com.api.agb.itera.exception.CorreoDuplicadoException;
import com.api.agb.itera.model.Rol;
import com.api.agb.itera.model.Usuario;
import com.api.agb.itera.repository.UsuarioRepository;
import com.api.agb.itera.utils.GenerateCodeUtil;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.security.SecureRandom;

@Slf4j
@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Autowired
    public UsuarioService( UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, EmailService emailService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }


    @Transactional
    public List<UsuarioDto> getAllUsers() {
        return usuarioRepository.findAll()
                .stream()
                .map(UsuarioDto::toDto)
                .toList();
    }


    @Transactional
    public UsuarioAddResponse addUsuario(UsuarioAddRequest usuarioAddRequest){
        if (usuarioRepository.existsByCorreoIgnoreCase(usuarioAddRequest.correo())) {
            throw new CorreoDuplicadoException("El correo ya está registrado");
        }
        Rol rol = new Rol(usuarioAddRequest.rol().id(), usuarioAddRequest.rol().nombre(), usuarioAddRequest.rol().description());
        Usuario tempUsuario = Usuario.builder()
                            .nombre(usuarioAddRequest.nombre())
                            .apellidoPaterno(usuarioAddRequest.apellidoPaterno())
                            .apellidoMaterno(usuarioAddRequest.apellidoMaterno())
                                            .correo(usuarioAddRequest.correo())
                .estado((short) 1)
                .rol(rol).build();

        // Contrasena temporal (generada aleatoriamente)
        String passwordTemp = generateRandomPassword(12);

        tempUsuario.setContrasena(passwordEncoder.encode(passwordTemp));

        Usuario savedUsuario = usuarioRepository.save(tempUsuario);
        emailService.sendWelcomeEmail(savedUsuario);
        return UsuarioAddResponse.toDto(savedUsuario);
    }

    @Transactional
    public UsuarioAddResponse updateUsuario(UsuarioEditRequest usuarioEditRequest){
       Usuario usuario = usuarioRepository.findById(usuarioEditRequest.id()).orElseThrow(()->  new EntityNotFoundException("Usuario no encontrado"));

         if (usuarioRepository.existsByCorreoAndIdNot(usuarioEditRequest.correo(), usuarioEditRequest.id())) {
             throw new IllegalArgumentException("Ya existe otro usuario con ese correo");
         }

         usuario.setNombre(usuarioEditRequest.nombre());
         usuario.setApellidoPaterno(usuarioEditRequest.apellidoPaterno());
         usuario.setApellidoMaterno(usuarioEditRequest.apellidoMaterno());
         usuario.setCorreo(usuarioEditRequest.correo());
         usuario.setRol(usuarioEditRequest.rol());

         Usuario usuarioUpdated = usuarioRepository.save(usuario);
         return UsuarioAddResponse.toDto(usuarioUpdated);
     }


    @Transactional
    public UsuarioAddResponse updateUsuarioStatus(UsuarioEditRequest request) {
         Long id = request.id();
         Usuario usuario = usuarioRepository.findById(id)
                 .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con id: " + id));

         usuario.setEstado(request.estado());

         Usuario usuarioUpdated = usuarioRepository.save(usuario);
         return UsuarioAddResponse.toDto(usuarioUpdated);
     }


    public void auth(LoginRequest loginRequest){
        Optional<Usuario> usuario = usuarioRepository.findByCorreoIgnoreCase(loginRequest.correo());
        log.info("Correo: {}", loginRequest.correo());
        if(usuario.isPresent()){
            Usuario u = usuario.get();
            if(passwordEncoder.matches(loginRequest.contrasena(), u.getContrasena())){
                String code = GenerateCodeUtil.generateCode();
                u.setOtp(code);
                usuarioRepository.save(u);
                emailService.sendOtpEmail(u.getCorreo(), code);
            } else{
                throw new BadCredentialsException("Bad credentials");
            }
        } else{
            throw new BadCredentialsException("Bad credentials");
        }
    }

    public boolean validateOtp(OtpValidateRequest otpToValidate){
        Optional<Usuario> usuario = usuarioRepository.findByCorreoIgnoreCase(otpToValidate.correo());
        if(usuario.isPresent()){
            String userOtp = usuario.get().getOtp();
            return otpToValidate.code().equals(userOtp);
        }
        return false;
    }

    // Genera una contraseña aleatoria con la longitud especificada
    private String generateRandomPassword(int length) {
        final String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-_+=<>?";
        SecureRandom rnd = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        }
        return sb.toString();
    }
}
