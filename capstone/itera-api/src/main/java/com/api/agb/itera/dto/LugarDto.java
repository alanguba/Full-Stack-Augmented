package com.api.agb.itera.dto;

import com.api.agb.itera.model.Lugar;

public record LugarDto(
        Long id,

        String name,

        String direction,

        String category
) {
    public static LugarDto toDto(Lugar lugar){
        return new LugarDto(
                lugar.getId(),
                lugar.getNombre(),
                lugar.getDireccion(),
                lugar.getCategoria()
        );
    }
}
