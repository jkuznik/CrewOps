package pl.crewops.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.crypto.SecretKey;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import pl.crewops.model.auth.RoleGrantedAuthority;
import pl.crewops.model.auth.RoleType;
import pl.crewops.security.config.SecurityConfigProperties;
import pl.crewops.security.custom.CustomUserPrincipal;

@Component
public abstract class JwtClaimsProvider {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final SecurityConfigProperties securityConfigProperties;
    private final long expirationTime;
    private final String jwtSecret;

    public JwtClaimsProvider(SecurityConfigProperties securityConfigProperties) {
        this.securityConfigProperties = securityConfigProperties;
        expirationTime = securityConfigProperties.getJwtExpiration();
        jwtSecret = securityConfigProperties.getJwtSecret();

        System.out.println("JwtClaimsProvider created with expiration time " + expirationTime);
    }

    public String generateToken(UserDetails userDetails) {
        var userPrincipal = (CustomUserPrincipal) userDetails;

        Map<String, Object> claims = new HashMap<>();
        claims.put("companyId", userPrincipal.getCompanyId());
        claims.put(
                "authorities",
                userPrincipal.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList()));
        return Jwts.builder()
                .claims()
                .subject(userPrincipal.getEmployeeId().toString())
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plusSeconds(expirationTime)))
                .add(claims)
                .and()
                .signWith(getKey(jwtSecret))
                .compact();
    }

    public UUID extractEmployeeId(String token) {
        return UUID.fromString(extractClaim(token, Claims::getSubject));
    }

    public UUID extractCompanyId(String token) {
        return UUID.fromString((String) extractClaim(token, claims -> claims.get("companyId")));
    }

    public Date extractExpiresAt(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public Set<RoleGrantedAuthority> extractAuthorities(String token) {
        Object authoritiesClaim = extractClaim(token, claims -> claims.get("authorities"));
        if (authoritiesClaim instanceof List<?>) {
            return ((List<?>) authoritiesClaim)
                    .stream()
                            .filter(obj -> obj instanceof String)
                            .map(obj -> new RoleGrantedAuthority(RoleType.valueOf(((String) obj).replace("ROLE_", ""))))
                            .collect(Collectors.toSet());
        }
        return Collections.emptySet();
    }

    public String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
            throw new IllegalArgumentException("No JWT token found in request");
        }

        return bearerToken.substring(7);
    }

    public boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    private Claims extractAllClaims(String token) {
        // This logic allow to reuse this abstraction of JwtClaimsProvider by BE and FE.
        // For BE side jwtSecret have to exist for sure - in other case it won't be allowed to generate token.
        // For FE side is not recommend to store jwtSecret and below implementation is good enough.
        if (jwtSecret != null) {
            return parseClaimsWithValidation(token);
        } else {
            return parseClaimsWithoutValidation(token);
        }
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    private Claims parseClaimsWithValidation(String token) {
        return Jwts.parser()
                .verifyWith(getKey(jwtSecret))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private Claims parseClaimsWithoutValidation(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                return null;
            }
            String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]));
            Map<String, Object> claimsMap = objectMapper.readValue(payloadJson, Map.class);
            return Jwts.claims().add(claimsMap).build();
        } catch (Exception e) {
            return null;
        }
    }

    private SecretKey getKey(String jwtSecret) {
        if (jwtSecret == null) {
            return null;
        }
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
