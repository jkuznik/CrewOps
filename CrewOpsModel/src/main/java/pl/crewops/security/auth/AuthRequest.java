package pl.crewops.security.auth;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record AuthRequest(@NotNull String username, @NotNull String password) {}
