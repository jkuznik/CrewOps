package pl.crewops.domain.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static pl.crewops.model.auth.RoleType.ADMIN;
import static pl.crewops.model.auth.RoleType.EMPLOYEE;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import pl.crewops.auth.*;
import pl.crewops.exception.UsernameAlreadyExistException;
import pl.crewops.model.Employee;
import pl.crewops.model.auth.AuthUser;
import pl.crewops.model.auth.Role;
import pl.crewops.security.jwt.JwtService;

@SpringJUnitConfig(
        classes = {
            AuthService.class,
            JwtService.class,
            AuthUserRepository.class,
            RoleRepository.class,
            PasswordEncoder.class
        })
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private AuthUserRepository authUserRepository;

    @MockitoBean
    private RoleRepository roleRepository;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private HttpServletRequest request;

    @MockitoBean
    private HttpServletResponse response;

    @Test
    void getByUsername_shouldReturnAuthUser_whenUserExists() {
        // given
        var authUser = AuthUser.builder()
                .username("username")
                .password("password")
                .roles(new HashSet<>())
                .build();

        // when
        when(authUserRepository.findByUsername("username")).thenReturn(Optional.of(authUser));

        AuthUser result =
                authService.getByUsername("username").orElseThrow(() -> new UsernameNotFoundException("username"));

        // then
        assertThat(result).isEqualTo(authUser);
        assertThat(result.getPassword()).isEqualTo("password");
    }

    @Test
    void createAuthUser_shouldReturnAuthUser_whenParamsAreValid() {
        // given
        var createAuthUserDTO = CreateAuthUserDTO.builder()
                .username("username")
                .password("password")
                .roles(new HashSet<>())
                .build();
        var employee = Employee.builder().build();
        var role = Role.builder().name(EMPLOYEE.name()).build();
        var authUser = AuthUser.builder()
                .username("username")
                .password("password")
                .roles(Set.of(role))
                .employee(employee)
                .build();

        // when
        when(passwordEncoder.encode("password")).thenReturn("password");
        when(roleRepository.findById(any())).thenReturn(Optional.of(role));
        when(authUserRepository.save(any())).thenReturn(authUser);

        AuthUser result = authService.createAuthUser(createAuthUserDTO, employee);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getPassword()).isEqualTo("password");
        assertThat(result.getRoles()).isEqualTo(Set.of(role));
    }

    @Test
    void login_shouldReturnAuthResponse_whenLoginSuccess() {
        // given
        String rawPassword = "plainPassword";
        String encodedPassword = "encodedPassword";
        String token = "jwt-token";

        var role = Role.builder().name(EMPLOYEE.name()).build();
        var authUser = AuthUser.builder()
                .username("username")
                .password(encodedPassword)
                .roles(Set.of(role))
                .employee(Employee.builder()
                        .firstName("firstName")
                        .lastName("lastName")
                        .build())
                .build();

        var authRequest =
                AuthRequest.builder().username("username").password(rawPassword).build();

        // when
        when(authUserRepository.findByUsername("username")).thenReturn(Optional.of(authUser));
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(true);
        when(jwtService.generateToken(any())).thenReturn(token);

        AuthResponse result = authService.login(authRequest, response);

        // then
        assertThat(result).isNotNull();
        assertThat(result.token()).isEqualTo(token);
    }

    @Test
    void login_shouldThrowException_whenLoginWithInvalidCredentials() {
        // given
        String rawPassword = "wrongPassword";
        String encodedPassword = "correctEncodedPassword";

        var authUser = AuthUser.builder()
                .username("username")
                .password(encodedPassword)
                .roles(new HashSet<>())
                .build();

        var authRequest =
                AuthRequest.builder().username("username").password(rawPassword).build();

        // when
        when(authUserRepository.findByUsername("username")).thenReturn(Optional.of(authUser));
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(false);

        // then
        var result = Assertions.catchException(() -> authService.login(authRequest, response));

        assertThat(result).isExactlyInstanceOf(IllegalArgumentException.class);
        assertThat(result.getMessage()).isEqualTo("Invalid username or password");
    }

    @Test
    void createAuthUser_shouldThrowException_whenUsernameAlreadyExists() {
        // given
        var authUser = AuthUser.builder()
                .username("username")
                .password("password")
                .roles(new HashSet<>())
                .build();

        var createAuthUserDTO = AuthTestFactory.createAuthUserDTO();

        var employee = AuthTestFactory.createEmployeeWithoutQualificationsAndVehicles();

        // when
        when(authUserRepository.findByUsername("username")).thenReturn(Optional.of(authUser));
        Exception result = Assertions.catchException(() -> authService.createAuthUser(createAuthUserDTO, employee));

        // then
        assertThat(result).isExactlyInstanceOf(UsernameAlreadyExistException.class);
    }

    @Test
    void validateToken_shouldReturnValidTokenResponse_whenTokenIsValid() {
        // given
        var username = "username";

        var authUser = AuthUser.builder()
                .username("username")
                .password("admin")
                .employee(new Employee())
                .roles(Set.of(Role.builder().name(ADMIN.name()).build()))
                .build();

        var validTokenRequest = ValidTokenRequest.builder().token("token").build();
        // when
        when(jwtService.extractUsername(any())).thenReturn(username);
        when(authUserRepository.findByUsername(any())).thenReturn(Optional.of(authUser));
        when(jwtService.validateToken(any(), any())).thenReturn(true);
        when(jwtService.extractExpiresAt(any())).thenReturn(new Date());

        ValidTokenResponse result = authService.validateToken(validTokenRequest);

        // then
        assertThat(result).isNotNull();
        assertThat(result.valid()).isTrue();
    }
}
