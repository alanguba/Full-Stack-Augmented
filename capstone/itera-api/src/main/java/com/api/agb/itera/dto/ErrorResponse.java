package com.api.agb.itera.dto;

public record ErrorResponse(
        int status,
        String error,
        String message
) {
}