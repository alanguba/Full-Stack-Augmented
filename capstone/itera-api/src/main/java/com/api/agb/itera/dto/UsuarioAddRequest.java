package com.api.agb.itera.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UsuarioAddRequest(
        @NotBlank(message = "El correo es obligatorio") @Email @Size(max = 100)
        String correo,

        @NotBlank(message = "El nombre es obligatorio") @Size(max = 255)
        String nombre,

        @NotBlank(message = "El apellido paterno es obligatorio") @Size(max = 255)
        String apellidoPaterno,

        @Size(max = 255)
        String apellidoMaterno,

        @NotNull(message = "El rol es obligatorio")
        RolDto rol
) {
}
