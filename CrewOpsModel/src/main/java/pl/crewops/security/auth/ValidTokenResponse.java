package pl.crewops.security.auth;

import java.util.Date;
import lombok.Builder;

@Builder
public record ValidTokenResponse(Boolean valid, Date expiration) {}
