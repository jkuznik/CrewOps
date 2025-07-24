package pl.crewops.auth;

import java.util.Date;
import lombok.Builder;

@Builder
public record ValidTokenResponse(Boolean valid, Date expiration) {}
