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

    private final JwtService jwtService;
    private final AuthUserRepository authUserRepository;

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
            return new AuthResponse(token);
        } else {
            throw new IllegalArgumentException("Invalid username or password");
        }
    }
}
