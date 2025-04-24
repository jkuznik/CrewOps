package pl.crewops.domain.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static pl.crewops.auth.RoleType.EMPLOYEE;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import pl.crewops.auth.AuthRequest;
import pl.crewops.auth.AuthResponse;
import pl.crewops.auth.CreateAuthUserDTO;
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
    void shouldReturnAuthUser_whenUserExists() {
        // given
        var authUser = AuthUser.builder()
                .username("username")
                .password("password")
                .roles(new HashSet<>())
                .build();

        // when
        when(authUserRepository.findByUsername("username")).thenReturn(Optional.of(authUser));

        AuthUser result = authService.getByUsername("username");

        // then
        assertThat(result).isEqualTo(authUser);
        assertThat(result.getPassword()).isEqualTo("password");
    }

    @Test
    void shouldReturnAuthUser_whenParamsAreValid() {
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
    void shouldReturnAuthResponse_whenLoginSuccess() {
        // given
        String rawPassword = "plainPassword";
        String encodedPassword = "encodedPassword";
        String token = "jwt-token";

        var role = Role.builder().name(EMPLOYEE.name()).build();
        var authUser = AuthUser.builder()
                .username("username")
                .password(encodedPassword)
                .roles(Set.of(role))
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
        verify(response).setHeader("Authorization", "Bearer " + token);
    }

    @Test
    void shouldThrowException_whenLoginWithInvalidCredentials() {
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
}
