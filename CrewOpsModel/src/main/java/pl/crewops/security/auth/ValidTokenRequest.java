package pl.crewops.security.auth;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record ValidTokenRequest(@NotNull String token) {}
