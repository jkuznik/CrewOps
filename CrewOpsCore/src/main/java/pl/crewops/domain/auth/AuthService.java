package pl.crewops.domain.auth;

import jakarta.servlet.http.HttpServletResponse;
import java.util.*;
import javax.management.relation.RoleNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pl.crewops.auth.*;
import pl.crewops.domain.employee.EmployeeAPI;
import pl.crewops.domain.tenant.TenantAPI;
import pl.crewops.dto.employee.CreateEmployeeDTO;
import pl.crewops.dto.employee.EmployeeDTO;
import pl.crewops.dto.employee.UpdateEmployeeDTO;
import pl.crewops.exception.auth.AuthUserNotFoundException;
import pl.crewops.exception.auth.UsernameAlreadyExistException;
import pl.crewops.infrastructure.multitenancy.TenantContext;
import pl.crewops.model.Employee;
import pl.crewops.model.publicSchema.AuthUser;
import pl.crewops.model.publicSchema.Role;
import pl.crewops.model.publicSchema.Tenant;
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
    private final TenantAPI tenantAPI;
    private final EmployeeAPI employeeAPI;

    @Override
    @Transactional(readOnly = true)
    public Optional<AuthUser> getByUsername(String username) {
        return authUserRepository.findByUsername(username);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AuthUser> getByEmployeeId(UUID employeeId) {
        return authUserRepository.findByEmployeeId(employeeId);
    }

    @Override
    @Transactional(readOnly = true)
    public Employee getEmployeeById(UUID employeeId) {
        return employeeAPI.getEmployeeById(employeeId);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public EmployeeDTO createAuthUserWithRelatedEmployee(CreateEmployeeDTO createEmployeeDTO) {
        log.info("Create employee current tenant is: {}", TenantContext.getCurrentTenant());
        if (getByUsername(createEmployeeDTO.username()).isPresent()) {
            throw new UsernameAlreadyExistException(createEmployeeDTO.username());
        }

        try {
            EmployeeDTO employee = employeeAPI.createEmployee(createEmployeeDTO);
            var createAuthUser = CreateAuthUserDTO.builder()
                    .username(createEmployeeDTO.username())
                    .password(createEmployeeDTO.password())
                    .roles(createEmployeeDTO.roles())
                    .build();
            createAuthUser(createAuthUser, employee.id(), createEmployeeDTO.companyId());
            log.info("Create employee {}", createEmployeeDTO);
            return employee;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public void deleteEmployee(UUID employeeId) {
        var employee = employeeAPI.getEmployeeById(employeeId);

        var authUser = getByEmployeeId(employeeId).orElseThrow(() -> new AuthUserNotFoundException(employee));

        employee.setActive(false);
        log.info("Delete authUser {}", authUser.getUsername());
        deleteById(authUser.getId());
        log.info("Set 'active' column to 'false' for employee {}", employeeId);
        employeeAPI.updateEmployee(UpdateEmployeeDTO.builder()
                .employeeId(employeeId)
                .department(employee.getDepartment())
                .phoneNumber(employee.getPhoneNumber())
                .build());
    }

    @Transactional
    public AuthUser createAuthUser(CreateAuthUserDTO createAuthUserDTO, UUID employeeId, UUID companyId) {
        if (getByUsername(createAuthUserDTO.username()).isPresent()) {
            log.error("Username " + createAuthUserDTO.username() + " already exists");
            throw new UsernameAlreadyExistException("Username " + createAuthUserDTO.username() + " already exists");
        }
        try {
            Tenant tenant = tenantAPI.getByCompanyId(companyId);
            var authUser = new AuthUser();
            authUser.setUsername(createAuthUserDTO.username());
            authUser.setPassword(passwordEncoder.encode(createAuthUserDTO.password()));
            authUser.setTenant(tenant);
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

    @Transactional(readOnly = true)
    public ValidTokenResponse validateToken(ValidTokenRequest validTokenRequest) {
        try {
            var authUser = authUserRepository
                    .findByEmployeeId(jwtService.extractEmployeeId(validTokenRequest.token()))
                    .orElseThrow(() ->
                            new UsernameNotFoundException("Username " + validTokenRequest.token() + " not found"));
            var userPrincipal = new UserPrincipal(authUser);
            boolean result = jwtService.validateToken(validTokenRequest.token(), userPrincipal);
            if (result) {
                Date expiresAt = jwtService.extractExpiresAt(validTokenRequest.token());
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

    @Transactional(readOnly = true)
    public AuthResponse login(AuthRequest authRequest, HttpServletResponse response) {
        try {
            AuthUser authUser = getByUsername(authRequest.username())
                    .orElseThrow(() -> new UsernameNotFoundException(authRequest.username()));
            if (passwordEncoder.matches(authRequest.password(), authUser.getPassword())) {
                TenantContext.setCurrentTenant(authUser.getTenant().getSchemaName());
                return finalizeLoginAction(authUser);
            } else {
                log.error("Login failed");
                throw new IllegalArgumentException("Invalid username or password");
            }
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("Login failed");
            throw new RuntimeException();
        }
    }

    private AuthResponse finalizeLoginAction(AuthUser authUser) {
        try {
            var userPrincipal = new UserPrincipal(authUser);
            String token = jwtService.generateToken(userPrincipal);
            log.info("Login successful for user: {}", authUser.getUsername());
            return new AuthResponse(token);
        } finally {
            TenantContext.clear();
        }
    }
}
