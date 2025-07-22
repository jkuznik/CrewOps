package pl.crewops.security.custom;

import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import pl.crewops.security.providers.CustomProvider;

@Component
@AllArgsConstructor
public class CustomAuthenticationManager implements AuthenticationManager {

    private final CustomProvider customProvider;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (customProvider.supports(authentication.getClass())) {
            return customProvider.authenticate(authentication);
        }

        return authentication;
    }
}
