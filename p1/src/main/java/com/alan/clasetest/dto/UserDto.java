package com.alan.clasetest.dto;

public record UserDto(
        Long id,
        String firstName,
        String lastName,
        String email
) {
}