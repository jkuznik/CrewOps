package pl.crewops.auth;

import java.util.Date;
import lombok.Builder;

@Builder
// TODO: update application to https
public record ValidTokenResponse(Boolean valid, Date expiration) {}
