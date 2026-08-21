package com.api.agb.itera.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.List;

public record PlanByIdResponse(
        Long id,

        @NotBlank
        @Size(min= 8, max=100)
        String name,

        @NotBlank
        @Size(min = 8, max = 255)
        String destination,

        @NotNull
        @Positive
        int days,

        @NotBlank
        @Size(min = 8, max = 255)
        String pace,

        String url,

        List<LugarDto> lugares,

        List<ItinerarioDto> itinerary
) {

}
