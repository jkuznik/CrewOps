package pl.crewops.security.auth;

import jakarta.validation.constraints.NotNull;

public record AuthResponse(@NotNull String token) {}
