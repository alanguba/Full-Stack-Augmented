package com.api.agb.itera.dto;

import jakarta.validation.constraints.*;
import java.util.List;

public record TripPlanRequest(
        @NotBlank(message = "El destino es obligatorio") String destination,
        @Min(value = 1, message = "El número de días debe ser al menos 1") @Max(value = 30, message = "El número de días no puede exceder 30") int days,
        @NotBlank(message = "El ritmo es obligatorio") String pace,
        @NotNull(message = "La lista de lugares es obligatoria") @Size(min = 1, message = "Debe haber al menos un lugar en el plan") List<PlaceDto> places
) {
    public record PlaceDto(
            @NotBlank(message = "El ID del lugar es obligatorio") String id,
            @NotBlank(message = "El nombre del lugar es obligatorio") String name,
            @NotBlank(message = "La dirección del lugar es obligatoria") String address,
            @NotBlank(message = "La categoría del lugar es obligatoria") String category
    ) {}
}
