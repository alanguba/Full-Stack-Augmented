package com.api.agb.itera.dto;

import jakarta.validation.constraints.NotBlank;

public record OtpValidateRequest(
        @NotBlank(message = "El correo es obligatorio")
        String correo,
        @NotBlank(message = "El código es obligatorio")
        String code
) {
}
