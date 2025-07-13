package pl.crewops.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.crewops.auth.ValidTokenRequest;
import pl.crewops.auth.ValidTokenResponse;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.model.auth.RoleGrantedAuthority;

@Slf4j
@Service
public class JwtService {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final CoreAPI coreAPI;

    public JwtService(CoreAPI coreAPI) {
        this.coreAPI = coreAPI;
    }

    public Map<String, Object> extractClaims(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                return Collections.emptyMap();
            }

            String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]));
            return objectMapper.readValue(payloadJson, Map.class);

        } catch (Exception e) {
            return Collections.emptyMap();
        }
    }

    public UUID getTenantCompanyId(String token) {
        return UUID.fromString((String) extractClaims(token).get("tenantCompanyId"));
    }

    public UUID getEmployeeId(String token) {
        return UUID.fromString((String) extractClaims(token).get("sub"));
    }

    public Date getExpiration(String token) {
        Object exp = extractClaims(token).get("exp");
        if (exp instanceof Number) {
            return new Date(((Number) exp).longValue() * 1000);
        }
        return null;
    }

    public Set<RoleGrantedAuthority> getAuthorities(String token) {
        Object authoritiesClaim = extractClaims(token).get("authorities");
        if (authoritiesClaim instanceof List<?>) {
            return ((List<?>) authoritiesClaim)
                    .stream()
                            .filter(obj -> obj instanceof String)
                            .map(obj -> new RoleGrantedAuthority((String) obj))
                            .collect(Collectors.toSet());
        }
        return Collections.emptySet();
    }

    public boolean isExpired(String token) {
        Date expiration = getExpiration(token);
        return expiration != null && expiration.before(new Date());
    }

    public boolean validToken(String token) {
        if (token == null) {
            return false;
        }
        var validTokenResponse =
                coreAPI.validateToken(new ValidTokenRequest(token)).orElse(new ValidTokenResponse(false, new Date()));

        return validTokenResponse.valid();
    }
}
