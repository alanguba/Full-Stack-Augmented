package com.api.agb.itera.dto;

public record FieldValidationError(
        String field,
        String message
) {
}
