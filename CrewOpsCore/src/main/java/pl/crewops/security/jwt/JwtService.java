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
import pl.crewops.model.auth.RoleGrantedAuthority;
import pl.crewops.security.config.SecurityConfigProperties;
import pl.crewops.security.custom.CustomUserPrincipal;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {

    private final SecurityConfigProperties securityConfigProperties;

    public String generateToken(UserDetails userDetails) {
        var userPrincipal = (CustomUserPrincipal) userDetails;

        Map<String, Object> claims = new HashMap<>();
        claims.put("firstName", userPrincipal.getFirstName());
        claims.put("lastName", userPrincipal.getLastName());
        claims.put("id", userPrincipal.getId());
        claims.put(
                "authorities",
                userPrincipal.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList()));
        return Jwts.builder()
                .claims()
                .subject(userDetails.getUsername())
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plusSeconds(securityConfigProperties.getJwtExpiration())))
                .add(claims)
                .and()
                .signWith(getKey())
                .compact();
    }

    public String extractUsername(String token) {
        // using extractClaim we can extract the username, or any other claim (those defaults and each custom claim we
        // create), from jwt token
        return extractClaim(token, Claims::getSubject);
    }

    public String extractFirstName(String token) {
        return extractClaim(token, claims -> claims.get("firstName", String.class));
    }

    public String extractLastName(String token) {
        return extractClaim(token, claims -> claims.get("lastName", String.class));
    }

    public UUID extractId(String token) {
        return extractClaim(token, claims -> claims.get("id", UUID.class));
    }

    public Collection<? extends GrantedAuthority> extractAuthorities(String token) {
        Set<?> rawAuthorities = extractClaim(token, claims -> claims.get("authorities", Set.class));

        if (rawAuthorities == null) {
            return Collections.emptySet();
        }

        return rawAuthorities.stream()
                .map(String::valueOf)
                .map(RoleGrantedAuthority::new)
                .collect(Collectors.toSet());
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
        final String userName = extractUsername(token);
        return (userName.equals(userDetails.getUsername()) && !isTokenExpired(token));
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
        log.info("JWT SecretKey initialized");
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
