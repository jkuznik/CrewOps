package pl.crewops.security.jwt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import pl.crewops.security.custom.CustomAuthentication;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthProvider implements AuthenticationProvider {

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        var customAuthentication = (CustomAuthentication) authentication;

        customAuthentication.setAuthenticated(true);

        return customAuthentication;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return CustomAuthentication.class.equals(authentication);
    }
}
