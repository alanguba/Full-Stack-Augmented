package com.api.agb.itera.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record RolUpdateRequest(
        @NotNull(message = "El id es obligatorio")
        @Positive
        Long id,

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max=100)
        String nombre,

        @NotBlank(message = "La descripción es obligatoria")
        @Size(max=255)
        String description
) {
}
