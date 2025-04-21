package pl.crewops.domain.auth;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.crewops.auth.AuthRequest;
import pl.crewops.auth.AuthResponse;
import pl.crewops.model.auth.AuthUser;
import pl.crewops.security.custom.UserPrincipal;
import pl.crewops.security.jwt.JwtService;

@Service
@RequiredArgsConstructor
class AuthService implements AuthAPI {

    //    private final SecurityConfigProperties securityConfigProperties;
    private final JwtService jwtService;

    private final AuthUserRepository authUserRepository;
    //    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public AuthUser getByUsername(String username) {
        return authUserRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));
    }

    @Transactional
    public AuthResponse login(@NotNull AuthRequest authRequest, HttpServletResponse response) {
        AuthUser byUsername = getByUsername(authRequest.username());

        if (byUsername.getPassword().equals(authRequest.password())) {
            var userPrincipal = new UserPrincipal(byUsername);
            String token = jwtService.generateToken(userPrincipal);
            response.setHeader("Authorization", "Bearer " + token);
            //            String refreshToken = generateRefreshToken(byUsername);
            return new AuthResponse(token /*, refreshToken*/);
        } else {
            throw new IllegalArgumentException("Invalid username or password");
        }
    }

    //    private String generateRefreshToken(AuthUser authUser) {
    //        if (authUser.getRefreshToken() != null) {
    //            refreshTokenRepository.delete(authUser.getRefreshToken());
    //            authUser.setRefreshToken(null);
    //        }
    //        var refreshToken = new RefreshToken();
    //        refreshToken.setRefreshToken(UUID.randomUUID().toString());
    //        refreshToken.setExpiresAt(
    //                Instant.now().plus(securityConfigProperties.getJwtRefreshTokenExpiration(), ChronoUnit.DAYS));
    //
    //        authUser.setRefreshToken(refreshToken);
    //        authUserRepository.save(authUser);
    //        return refreshToken.getRefreshToken();
    //    }
}
