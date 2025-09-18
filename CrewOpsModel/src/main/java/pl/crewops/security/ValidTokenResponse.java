package pl.crewops.security;

import java.util.Date;
import lombok.Builder;

@Builder
public record ValidTokenResponse(Boolean valid, Date expiration) {}
