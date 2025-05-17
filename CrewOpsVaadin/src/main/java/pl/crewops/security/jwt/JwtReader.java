package pl.crewops.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;
import pl.crewops.model.auth.RoleGrantedAuthority;

public class JwtReader {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static Map<String, Object> extractClaims(String token) {
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

    public static String getUsername(String token) {
        return (String) extractClaims(token).get("sub");
    }

    public static String getFirstName(String token) {
        return (String) extractClaims(token).get("firstName");
    }

    public static String getLastName(String token) {
        return (String) extractClaims(token).get("lastName");
    }

    public static Date getExpiration(String token) {
        Object exp = extractClaims(token).get("exp");
        if (exp instanceof Number) {
            return new Date(((Number) exp).longValue() * 1000);
        }
        return null;
    }

    public static Set<RoleGrantedAuthority> getAuthorities(String token) {
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

    public static boolean isExpired(String token) {
        Date expiration = getExpiration(token);
        return expiration != null && expiration.before(new Date());
    }

    public static String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
