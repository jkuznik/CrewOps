package pl.crewops.model.dto.auth;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record AuthRequest(@NotNull String username, @NotNull String password) {}
