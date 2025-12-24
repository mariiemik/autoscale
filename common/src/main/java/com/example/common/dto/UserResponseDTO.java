package com.example.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Пользователь")
public record UserResponseDTO(@Schema(description = "ID пользователя") String id,
                              @Schema(description = "Имя пользователя") String name,
                              @Schema(description = "email пользователя") String email) {
}

