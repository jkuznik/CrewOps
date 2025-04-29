package pl.crewops.domain.auth;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import java.util.Date;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.crewops.auth.*;
import pl.crewops.dto.employee.EmployeeDTO;
import pl.crewops.model.Employee;
import pl.crewops.model.auth.AuthUser;
import pl.crewops.model.auth.Role;
import pl.crewops.security.custom.UserPrincipal;
import pl.crewops.security.jwt.JwtService;

@Service
@Slf4j
@RequiredArgsConstructor
class AuthService implements AuthAPI {

    private final JwtService jwtService;
    private final AuthUserRepository authUserRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Optional<AuthUser> getByUsername(@NotNull String username) {
        return authUserRepository.findByUsername(username);
    }

    @Transactional
    public AuthUser createAuthUser(CreateAuthUserDTO createAuthUserDTO, Employee employee) {
        var authUser = new AuthUser();
        authUser.setUsername(createAuthUserDTO.username());
        authUser.setPassword(passwordEncoder.encode(createAuthUserDTO.password()));
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
        AuthUser byUsername = byUsername(authRequest.username());
        log.info("Login action by username: {}", byUsername);

        try {
            if (passwordEncoder.matches(authRequest.password(), byUsername.getPassword())) {
                var userPrincipal = new UserPrincipal(byUsername);
                String token = jwtService.generateToken(userPrincipal);
                Employee employee = userPrincipal.getAuthUser().getEmployee();
                var employeeDTO = EmployeeDTO.builder()
                        .firstName(employee.getFirstName())
                        .lastName(employee.getLastName())
                        .build();
                Date date = jwtService.extractExpiresAt(token);
                response.setHeader("Authorization", "Bearer " + token);
                log.info("Login successful, token: {}", token);
                return new AuthResponse(token, authRequest.username(), employeeDTO, date);
            } else {
                log.info("Login failed, wrong password");
                throw new IllegalArgumentException("Invalid username or password");
            }
        } catch (Exception e) {
            log.error("Login failed", e);
            throw new IllegalArgumentException("Invalid username or password");
        }
    }

    public ValidTokenResponse validateToken(@NotNull ValidTokenRequest validTokenRequest) {
        var authUser = byUsername(validTokenRequest.username());
        var userDetails = new UserPrincipal(authUser);
        boolean result = false;
        try {
            log.info("Token validation started");
            result = jwtService.validateToken(validTokenRequest.token(), userDetails);
        } catch (IllegalArgumentException e) {
            log.info("Token validation - token not exist");
        }
        return new ValidTokenResponse(result);
    }

    private AuthUser byUsername(String username) {
        return getByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));
    }
}
