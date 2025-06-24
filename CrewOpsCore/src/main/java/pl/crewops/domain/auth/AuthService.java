package pl.crewops.domain.auth;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletResponse;
import java.util.*;
import javax.management.relation.RoleNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.crewops.auth.*;
import pl.crewops.exception.UsernameAlreadyExistException;
import pl.crewops.model.publicSchema.AuthUser;
import pl.crewops.model.publicSchema.Role;
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
    public Optional<AuthUser> getByUsername(String username) {
        return authUserRepository.findByUsername(username);
    }

    @Override
    public Optional<AuthUser> getByEmployeeId(UUID employeeId) {
        return authUserRepository.findByEmployeeId(employeeId);
    }

    @Transactional
    public AuthUser createAuthUser(CreateAuthUserDTO createAuthUserDTO, UUID employeeId) {
        if (getByUsername(createAuthUserDTO.username()).isPresent()) {
            log.error("Username " + createAuthUserDTO.username() + " already exists");
            throw new UsernameAlreadyExistException("Username " + createAuthUserDTO.username() + " already exists");
        }
        try {
            var authUser = new AuthUser();
            authUser.setUsername(createAuthUserDTO.username());
            authUser.setPassword(passwordEncoder.encode(createAuthUserDTO.password()));
            Set<Role> roles = new HashSet<>();
            createAuthUserDTO.roles().forEach(role -> {
                try {
                    roles.add(roleRepository
                            .findByName(role.name())
                            .orElseThrow(() -> new RoleNotFoundException("Role " + role.name() + " not found")));
                } catch (RoleNotFoundException e) {
                    throw new RuntimeException(e);
                }
            });
            log.info("Creating auth user " + createAuthUserDTO.username() + " with roles " + roles);
            authUser.setRoles(roles);
            authUser.setEmployeeId(employeeId);
            log.info("Auth user instantiated successfully as " + authUser.toString());
            return authUserRepository.save(authUser);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException(e);
        }
    }

    @Transactional
    public void deleteById(UUID uuid) {
        authUserRepository.deleteById(uuid);
    }

    @Transactional
    public AuthResponse login(AuthRequest authRequest, HttpServletResponse response) {
        try {
            AuthUser byUsername = byUsername(authRequest.username());
            log.debug("Login action by username: {}", byUsername);
            if (passwordEncoder.matches(authRequest.password(), byUsername.getPassword())) {
                // TODO: in case when password is matches for this username there is necessary to know tenant id to
                // setSchema where employee with employeeId stored in AuthUser should exist
                var userPrincipal = new UserPrincipal(byUsername);
                String token = jwtService.generateToken(userPrincipal);
                log.debug("Login successful, token: {}", token);
                return new AuthResponse(token);
            } else {
                log.error("Login failed");
                throw new IllegalArgumentException("Invalid username or password");
            }
        } catch (Exception e) {
            log.error("Login failed");
            throw new IllegalArgumentException("Invalid username or password");
        }
    }

    public ValidTokenResponse validateToken(ValidTokenRequest validTokenRequest) {
        try {
            log.debug("Token validation started");
            AuthUser authUser;
            try {
                authUser = authUserRepository
                        .findByUsername(jwtService.extractUsername(validTokenRequest.token()))
                        .orElseThrow(() ->
                                new UsernameNotFoundException("Username " + validTokenRequest.token() + " not found"));
            } catch (ExpiredJwtException e) {
                return new ValidTokenResponse(false, null);
            }
            var userDetails = new UserPrincipal(authUser);
            boolean result = jwtService.validateToken(validTokenRequest.token(), userDetails);
            if (result) {
                Date expiresAt = jwtService.extractExpiresAt(validTokenRequest.token());
                log.debug("Token validation finished");
                return new ValidTokenResponse(true, expiresAt);
            } else {
                log.error("Token validation failed");
                return new ValidTokenResponse(false, null);
            }
        } catch (IllegalArgumentException e) {
            log.error("Token validation failed with exception");
            return new ValidTokenResponse(false, null);
        }
    }

    private AuthUser byUsername(String username) {
        return getByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));
    }
}
