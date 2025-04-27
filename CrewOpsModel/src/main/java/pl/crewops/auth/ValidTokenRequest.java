package pl.crewops.auth;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record ValidTokenRequest(@NotNull String token, @NotNull String username) {}
