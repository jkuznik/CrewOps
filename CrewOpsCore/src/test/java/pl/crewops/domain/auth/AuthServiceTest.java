package pl.crewops.domain.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static pl.crewops.model.auth.RoleType.EMPLOYEE;
import static pl.crewops.model.auth.RoleType.SYSTEM_ADMIN;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.util.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import pl.crewops.IntegrationTest;
import pl.crewops.auth.*;
import pl.crewops.domain.employee.EmployeeAPI;
import pl.crewops.domain.tenant.TenantAPI;
import pl.crewops.dto.employee.CreateEmployeeDTO;
import pl.crewops.exception.domain.auth.UsernameAlreadyExistException;
import pl.crewops.model.Employee;
import pl.crewops.model.publicSchema.AuthUser;
import pl.crewops.model.publicSchema.Role;
import pl.crewops.model.publicSchema.Tenant;
import pl.crewops.security.jwt.JwtServiceCore;

@SpringJUnitConfig(
        classes = {
            AuthService.class,
            JwtServiceCore.class,
            AuthUserRepository.class,
            RoleRepository.class,
            PasswordEncoder.class,
            EmployeeAPI.class,
            TenantAPI.class
        })
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @MockitoBean
    private JwtServiceCore jwtService;

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

    @MockitoBean
    private TenantAPI tenantAPI;

    @MockitoBean
    private EmployeeAPI employeeAPI;

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
        var tenant = new Tenant();
        tenant.setId(UUID.randomUUID());
        var createAuthUserDTO = CreateAuthUserDTO.builder()
                .username("username")
                .password("password")
                .roles(new HashSet<>())
                .build();
        UUID randomUUID = UUID.randomUUID();
        var employee = Employee.builder().build();
        employee.setId(randomUUID);
        var role = Role.builder().name(EMPLOYEE.name()).build();
        var authUser = AuthUser.builder()
                .username("username")
                .password("password")
                .roles(Set.of(role))
                .employeeId(randomUUID)
                .build();

        // when
        when(tenantAPI.getByCompanyId(any())).thenReturn(tenant);
        when(passwordEncoder.encode("password")).thenReturn("password");
        when(roleRepository.findById(any())).thenReturn(Optional.of(role));
        when(authUserRepository.save(any())).thenReturn(authUser);

        AuthUserDTO result = authService.createAuthUser(createAuthUserDTO, randomUUID, UUID.randomUUID());

        // then
        assertThat(result).isNotNull();
        assertThat(result.password()).isEqualTo("password");
        //        assertThat(result.roles()).isEqualTo(Set.of(role));
    }

    @Test
    void createAuthUserWithRelatedEmployee_ShouldThrowException_whenUsernameAlreadyExists() {
        // given
        var existedUsername = "existedUsername";
        CreateEmployeeDTO createEmployeeDTO = CreateEmployeeDTO.builder()
                .firstName("firstName")
                .lastName("lastName")
                .birthDate(LocalDate.parse("2000-01-01"))
                .phoneNumber("123456789")
                .department("department")
                .username(existedUsername)
                .password("password")
                .roles(Set.of())
                .companyId(IntegrationTest.TEST_TENANT_COMPANY_ID)
                .build();

        // when
        when(authUserRepository.findByUsername(existedUsername)).thenReturn(Optional.of(new AuthUser()));
        var result = catchException(() -> authService.createAuthUserWithRelatedEmployee(createEmployeeDTO));

        // then
        assertThat(result).isExactlyInstanceOf(UsernameAlreadyExistException.class);
    }

    @Test
    void login_shouldReturnAuthResponse_whenLoginSuccess() {
        // given
        String rawPassword = "plainPassword";
        String encodedPassword = "encodedPassword";
        String token = "jwt-token";
        Tenant tenant = Tenant.builder().companyId(UUID.randomUUID()).build();
        Employee employee =
                Employee.builder().firstName("firstName").lastName("lastName").build();

        var role = Role.builder().name(EMPLOYEE.name()).build();
        UUID randomUUID = UUID.randomUUID();
        var authUser = AuthUser.builder()
                .username("username")
                .password(encodedPassword)
                .tenant(tenant)
                .roles(Set.of(role))
                .employeeId(randomUUID)
                .build();

        var authRequest =
                AuthRequest.builder().username("username").password(rawPassword).build();

        // when
        when(authUserRepository.findByUsername("username")).thenReturn(Optional.of(authUser));
        when(employeeAPI.getEmployeeById(any())).thenReturn(employee);
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

        // when
        when(authUserRepository.findByUsername("username")).thenReturn(Optional.of(authUser));
        Exception result = Assertions.catchException(
                () -> authService.createAuthUser(createAuthUserDTO, UUID.randomUUID(), UUID.randomUUID()));

        // then
        assertThat(result).isExactlyInstanceOf(UsernameAlreadyExistException.class);
    }

    @Test
    void validateToken_shouldReturnValidTokenResponse_whenTokenIsValid() {
        // given
        var tenant = Tenant.builder().companyId(UUID.randomUUID()).build();

        var authUser = AuthUser.builder()
                .username("username")
                .password("admin")
                .tenant(tenant)
                .employeeId(UUID.randomUUID())
                .roles(Set.of(Role.builder().name(SYSTEM_ADMIN.name()).build()))
                .build();

        Employee employee =
                Employee.builder().firstName("firstName").lastName("lastName").build();

        var validTokenRequest = ValidTokenRequest.builder().token("token").build();
        // when
        when(jwtService.extractEmployeeId(any())).thenReturn(UUID.randomUUID());
        when(authUserRepository.findByEmployeeId(any())).thenReturn(Optional.of(authUser));
        when(employeeAPI.getEmployeeById(any())).thenReturn(employee);
        when(jwtService.validToken(any(), any())).thenReturn(true);
        when(jwtService.extractExpiresAt(any())).thenReturn(new Date());

        ValidTokenResponse result = authService.validateToken(validTokenRequest);

        // then
        assertThat(result).isNotNull();
        assertThat(result.valid()).isTrue();
    }

    @Test
    void terminateEmployeeAuthUserAccount_shouldSetEmployeeActiveToFalse_andDeleteAuthUser() {
        // given
        var employeeId = UUID.randomUUID();

        // when
        authService.terminateEmployeeAuthUserAccount(employeeId);

        verify(authUserRepository).deleteByEmployeeId(employeeId);
        verify(employeeAPI).updateEmployee(any());
    }
}
