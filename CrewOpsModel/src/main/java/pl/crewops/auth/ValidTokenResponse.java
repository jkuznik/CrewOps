package pl.crewops.auth;

import lombok.Builder;

@Builder
public record ValidTokenResponse(Boolean valid) {}
