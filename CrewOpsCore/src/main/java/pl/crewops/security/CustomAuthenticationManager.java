package pl.crewops.security;

import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import pl.crewops.security.jwt.JwtAuthProvider;

@Component
@AllArgsConstructor
public class CustomAuthenticationManager implements AuthenticationManager {

    private final JwtAuthProvider jwtAuthProvider;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        return jwtAuthProvider.authenticate(authentication);
    }
}
