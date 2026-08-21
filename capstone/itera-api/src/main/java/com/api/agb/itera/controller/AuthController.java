package com.api.agb.itera.controller;

import com.api.agb.itera.dto.ResetPasswordRequest;
import com.api.agb.itera.service.ResetPasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {
    final private ResetPasswordService resetPasswordService;

    @Autowired
    public AuthController(ResetPasswordService resetPasswordService) {
        this.resetPasswordService = resetPasswordService;
    }

    @PostMapping("/api/auth/reset-password")
    public void resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest) {
        resetPasswordService.resetPassword(resetPasswordRequest);
    }
}
