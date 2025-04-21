package pl.crewops.domain.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pl.crewops.model.auth.AuthUser;

@Service
@RequiredArgsConstructor
class AuthService implements AuthAPI {

    private final AuthUserRepository authUserRepository;

    @Override
    public AuthUser getByUsername(String username) {
        return authUserRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));
    }
}
