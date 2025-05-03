package pl.crewops.auth;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
// TODO: update application to https
public record AuthRequest(@NotNull String username, @NotNull String password) {}
