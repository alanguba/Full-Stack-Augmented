package com.api.agb.itera.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public record AllPlansByUserResponse(
        Long id,

        @NotBlank @Size(max = 100)
        String nombre,

        @NotBlank @Size(max = 255)
        String destino,

        @NotNull @Positive
        Integer dias,

        @NotBlank @Size(max = 255)
        String ritmo,

        @NotNull
        Short estado,

        String url,

        @NotNull
        Instant createdAt,

        @NotNull
        @Positive
        Integer lugaresCount
) {
}
