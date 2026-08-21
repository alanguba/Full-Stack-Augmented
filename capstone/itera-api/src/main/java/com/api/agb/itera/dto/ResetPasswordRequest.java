package com.api.agb.itera.dto;

import jakarta.validation.constraints.NotNull;

public record ResetPasswordRequest(
        @NotNull(message = "El correo es obligatorio")
        String email,
        @NotNull(message = "El código OTP es obligatorio")
        String otp,
        @NotNull(message = "La nueva contraseña es obligatoria")
        String newPassword
) {
}
