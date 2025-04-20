package pl.crewops.security.custom;

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
        if (jwtAuthProvider.supports(authentication.getClass())) {
            return jwtAuthProvider.authenticate(authentication);
        }

        return authentication;
    }
}
