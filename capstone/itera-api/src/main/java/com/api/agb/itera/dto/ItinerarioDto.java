package com.api.agb.itera.dto;

import com.api.agb.itera.model.Itinerario;

import java.util.ArrayList;
import java.util.List;

public record ItinerarioDto(
        Long id,

        Integer day,

        String title,

        String summary,

        List<ActividadDto> stops
) {
    public static ItinerarioDto toDto(Itinerario itinerario){
        List<ActividadDto> actividades = new ArrayList<>();
        itinerario.getActividades().forEach(actividad -> actividades.add(ActividadDto.toDto(actividad)));
        return new ItinerarioDto(
                itinerario.getId(),
                itinerario.getDia(),
                itinerario.getTitulo(),
                itinerario.getResumen(),
                actividades
        );
    }
}
