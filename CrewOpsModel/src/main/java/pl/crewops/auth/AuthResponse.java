package pl.crewops.auth;

import jakarta.validation.constraints.NotNull;

public record AuthResponse(@NotNull String token /*, @NotNull String refreshToken*/) {}
