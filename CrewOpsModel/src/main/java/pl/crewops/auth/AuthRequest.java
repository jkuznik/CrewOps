package pl.crewops.auth;

import jakarta.validation.constraints.NotNull;

public record AuthRequest(@NotNull String username, @NotNull String password) {}
