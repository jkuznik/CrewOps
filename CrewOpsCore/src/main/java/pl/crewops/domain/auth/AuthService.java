package pl.crewops.domain.auth;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.crewops.auth.AuthRequest;
import pl.crewops.auth.AuthResponse;
import pl.crewops.auth.CreateAuthUserDTO;
import pl.crewops.model.Employee;
import pl.crewops.model.auth.AuthUser;
import pl.crewops.model.auth.Role;
import pl.crewops.security.custom.UserPrincipal;
import pl.crewops.security.jwt.JwtService;

@Service
@RequiredArgsConstructor
class AuthService implements AuthAPI {

    private final JwtService jwtService;
    private final AuthUserRepository authUserRepository;
    private final RoleRepository roleRepository;

    @Override
    public AuthUser getByUsername(@NotNull String username) {
        return authUserRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));
    }

    @Transactional
    public AuthUser create(CreateAuthUserDTO createAuthUserDTO, Employee employee) {
        var authUser = new AuthUser();
        authUser.setUsername(createAuthUserDTO.username());
        authUser.setPassword(createAuthUserDTO.password());
        Set<Role> roles = new HashSet<>();
        createAuthUserDTO
                .roles()
                .forEach(role -> roles.add(roleRepository.findById(role.id()).orElseThrow()));
        authUser.setRoles(roles);
        authUser.setEmployee(employee);
        return authUserRepository.save(authUser);
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
