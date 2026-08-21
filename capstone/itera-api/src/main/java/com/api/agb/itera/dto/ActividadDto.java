package com.api.agb.itera.dto;

import com.api.agb.itera.model.Actividad;

public record ActividadDto(
        Long id,

        String time,

        String name,

        String note
) {
    public static ActividadDto toDto(Actividad actividad) {
        return new ActividadDto(
                actividad.getId(),
                actividad.getTiempo(),
                actividad.getNombre(),
                actividad.getNota()
        );
    }
}
