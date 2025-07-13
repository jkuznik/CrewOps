package pl.crewops.security.jwt;

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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import pl.crewops.security.config.SecurityConfigProperties;
import pl.crewops.security.custom.CustomUserPrincipal;
import pl.crewops.security.custom.UserPrincipal;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {

    private final SecurityConfigProperties securityConfigProperties;

    public String generateToken(UserDetails userDetails) {
        var userPrincipal = (CustomUserPrincipal) userDetails;

        Map<String, Object> claims = new HashMap<>();
        claims.put("tenantCompanyId", userPrincipal.getCompanyId());
        claims.put(
                "authorities",
                userPrincipal.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList()));
        return Jwts.builder()
                .claims()
                .subject(userPrincipal.getEmployeeId().toString())
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plusSeconds(securityConfigProperties.getJwtExpiration())))
                .add(claims)
                .and()
                .signWith(getKey())
                .compact();
    }

    public UUID extractEmployeeId(String token) {
        return UUID.fromString(extractClaim(token, Claims::getSubject));
    }

    public String extractTenantCompanyId(String token) {
        return extractClaim(token, claims -> claims.get("tenantCompanyId", String.class));
    }

    public Date extractExpiresAt(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
            throw new IllegalArgumentException("No JWT token found in request");
        }

        return bearerToken.substring(7);
    }

    // this method allow me to extract each claim from token
    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final UUID userName = extractEmployeeId(token);
        UserPrincipal principal = (UserPrincipal) userDetails;
        return (userName.equals(principal.getAuthUser().getEmployeeId()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private SecretKey getKey() {
        if (securityConfigProperties.getJwtSecret() == null) {
            log.info("JWT Secret is null");
            return null;
        }
        byte[] keyBytes = Decoders.BASE64.decode(securityConfigProperties.getJwtSecret());
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
