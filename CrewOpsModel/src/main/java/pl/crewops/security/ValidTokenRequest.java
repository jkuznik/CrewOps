package pl.crewops.security;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record ValidTokenRequest(@NotNull String token) {}
