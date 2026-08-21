package com.api.agb.itera.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public record PlanRequest(
        Long id,

        @NotBlank(message = "El nombre es obligatorio") @Size(max = 100)
        String nombre,

        @NotBlank(message = "El destino es obligatorio") @Size(max = 255)
        String destino,

        @NotNull(message = "El número de días es obligatorio") @Positive(message = "El número de días debe ser un valor positivo")
        Integer dias,

        @NotBlank(message = "El ritmo es obligatorio") @Size(max = 255)
        String ritmo,

        @NotNull(message = "El estado es obligatorio")
        Short estado,

        @NotNull(message = "La fecha de creación es obligatoria")
        Instant createdAt,

        @NotNull(message = "El ID del usuario es obligatorio")
        Long usuarioId

) {
}
