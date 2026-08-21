package com.api.agb.itera.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.List;

public record ItinerarySaveRequest(
        @NotBlank(message = "El nombre del itinerario es obligatorio")
        @Size(min= 8, max=100)
        String name,

        @NotNull(message = "El ID del usuario es obligatorio")
        @Positive
        Long usuario_id,

        @NotBlank(message = "El destino es obligatorio")
        @Size(min = 8, max = 255)
        String destination,

        @NotNull(message = "El número de días es obligatorio")
        @Positive
        int days,

        @NotBlank(message = "El ritmo del viaje es obligatorio")
        @Size(min = 8, max = 255)
        String pace,

        List<ItinerarySaveRequest.Lugares> lugares,

        List<ItinerarySaveRequest.DayPlan> itinerary
) {
    public record Lugares(
            @NotBlank
            String name,

            @NotBlank
            String direction,

            @NotBlank
            String category
    ){}
    public record DayPlan(
            @NotNull
            @Positive
            int day,

            @NotBlank
            @Size(min = 8, max = 255)
            String title,

            @NotBlank
            String summary,

            List<ItinerarySaveRequest.Stop> stops
    ) {}
    public record Stop(
            @NotBlank
            @Size(min = 8, max = 255)
            String time,

            @NotBlank
            @Size(min = 8, max = 255)
            String name,

            @NotBlank
            @Size(min = 8, max = 255)
            String note
    ) {}
}