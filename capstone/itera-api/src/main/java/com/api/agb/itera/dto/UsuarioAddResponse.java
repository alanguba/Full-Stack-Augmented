package com.api.agb.itera.dto;

import com.api.agb.itera.model.Usuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UsuarioAddResponse(
        Long id,

        @NotBlank @Email @Size(max = 100)
        String correo,

        @NotBlank @Size(max = 255)
        String nombre,

        @NotBlank @Size(max = 255)
        String apellidoPaterno,

        @Size(max = 255)
        String apellidoMaterno,

        @NotNull
        Short estado,

        @NotNull
        RolDto rol
) {
    public static UsuarioAddResponse toDto(Usuario usuario) {
        RolDto rol = new RolDto(usuario.getRol().getId(),
                usuario.getRol().getNombre(),
                usuario.getRol().getDescripcion());
        return new UsuarioAddResponse(
                usuario.getId(),
                usuario.getCorreo(),
                usuario.getNombre(),
                usuario.getApellidoPaterno(),
                usuario.getApellidoMaterno(),
                usuario.getEstado(),
                rol
        );
    }
}
