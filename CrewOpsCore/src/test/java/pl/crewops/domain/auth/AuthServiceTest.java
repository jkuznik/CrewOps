package pl.crewops.domain.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
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
        var role = Role.builder().name("role").build();
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
    void login() {}
}
