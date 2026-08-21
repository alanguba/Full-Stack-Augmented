package com.api.agb.itera.dto;

import com.api.agb.itera.model.Rol;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record RolDto(
        @NotNull
        @Positive
        Long id,

        @NotBlank
        @Size(max=100)
        String nombre,

        @NotBlank
        @Size(max=255)
        String description
) {
    public static RolDto toDto(Rol rol){
        return new RolDto(
                rol.getId(),
                rol.getNombre(),
                rol.getDescripcion()
        );
    }
}
