package com.api.agb.itera.dto;

import com.api.agb.itera.model.Usuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UsuarioDto(
        Long id,

        @NotBlank(message = "El correo es obligatorio") @Email @Size(max = 100)
        String correo,

        @NotBlank(message = "El nombre es obligatorio") @Size(max = 255)
        String nombre,

        @NotBlank(message = "El apellido paterno es obligatorio") @Size(max = 255)
        String apellidoPaterno,

        @Size(max = 255)
        String apellidoMaterno,

        @NotNull(message = "El estado es obligatorio")
        Short estado,

        @NotNull(message = "El rol es obligatorio")
        RolDto rol
) {
    public static UsuarioDto toDto(Usuario usuario){
        RolDto rol = new RolDto(usuario.getRol().getId(),
                usuario.getRol().getNombre(),
                usuario.getRol().getDescripcion());
        return new UsuarioDto(
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
