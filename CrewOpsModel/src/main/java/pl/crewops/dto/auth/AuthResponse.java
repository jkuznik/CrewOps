package pl.crewops.dto.auth;

import jakarta.validation.constraints.NotNull;

public record AuthResponse(@NotNull String token) {}
