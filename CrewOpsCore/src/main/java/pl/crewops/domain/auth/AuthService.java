package pl.crewops.domain.auth;

import static pl.crewops.util.credentialsGenerator.CredentialGenerator.generatePassword;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;
import javax.management.relation.RoleNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import pl.crewops.domain.employee.EmployeeAPI;
import pl.crewops.domain.message.MessageAPI;
import pl.crewops.domain.option.OptionAPI;
import pl.crewops.domain.tenant.TenantAPI;
import pl.crewops.enums.AuthUserOptions;
import pl.crewops.exception.domain.auth.AuthUserNotFoundException;
import pl.crewops.exception.domain.auth.UsernameAlreadyExistException;
import pl.crewops.infrastructure.multitenancy.TenantContext;
import pl.crewops.model.compositePK.AUOID;
import pl.crewops.model.dto.auth.*;
import pl.crewops.model.dto.auth.AuthRequest;
import pl.crewops.model.dto.auth.AuthResponse;
import pl.crewops.model.dto.employee.CreateEmployeeDTO;
import pl.crewops.model.dto.employee.EmployeeDTO;
import pl.crewops.model.dto.employee.UpdateEmployeeDTO;
import pl.crewops.model.dto.message.RecipientSelection;
import pl.crewops.model.dto.message.SendMessageCommand;
import pl.crewops.model.dto.option.AuthUserOptionDTO;
import pl.crewops.model.dto.tenant.TenantDTO;
import pl.crewops.model.publicSchema.AuthUser;
import pl.crewops.model.publicSchema.Option;
import pl.crewops.model.publicSchema.Role;
import pl.crewops.model.publicSchema.Tenant;
import pl.crewops.security.ValidTokenRequest;
import pl.crewops.security.ValidTokenResponse;
import pl.crewops.security.custom.UserPrincipal;
import pl.crewops.security.jwt.JwtServiceCore;
import pl.crewops.util.credentialsGenerator.CredentialGenerator;

@Service
@Slf4j
@RequiredArgsConstructor
class AuthService implements AuthAPI {

    private final JwtServiceCore jwtService;
    private final AuthUserRepository authUserRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final TenantAPI tenantAPI;
    private final EmployeeAPI employeeAPI;
    private final OptionAPI optionAPI;
    private final MessageAPI messageAPI;

    @PersistenceContext
    private EntityManager entityManager;

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
    public Set<AuthUserOptionDTO> getOptionsByEmployeeId(UUID employeeId) {
        AuthUser authUser = authUserRepository
                .findByEmployeeId(employeeId)
                .orElseThrow(() -> new AuthUserNotFoundException(employeeId));
        return optionAPI.getAuthUserOptionsByAuthUserId(authUser.getId());
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public CreateAuthUserResult createAuthUserWithRelatedEmployee(CreateEmployeeDTO createEmployeeDTO) {
        return getCreateAuthUserResult(createEmployeeDTO, false);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public CreateAuthUserResult createAuthUserWithRelatedEmployeeForRegisterCustomerRequirements(
            CreateEmployeeDTO createEmployeeDTO) {
        return getCreateAuthUserResult(createEmployeeDTO, true);
    }

    private CreateAuthUserResult getCreateAuthUserResult(
            CreateEmployeeDTO createEmployeeDTO, boolean withPlainPassword) {
        log.info("Create employee current tenant is: {}", TenantContext.getCurrentTenant());
        String username;
        do {
            username =
                    CredentialGenerator.generateUsername(createEmployeeDTO.firstName(), createEmployeeDTO.lastName());
        } while (getByUsername(username).isPresent());

        try {
            // until createEmployee is in this same with createAuthUser tx then hibernate handle rollback, check this
            // some time if any
            // implementation is required
            EmployeeDTO employee = employeeAPI.createEmployee(createEmployeeDTO);
            String generatedPassword = generatePassword();
            var createAuthUser = CreateAuthUserDTO.builder()
                    .username(username)
                    .password(generatedPassword)
                    .roles(createEmployeeDTO.roles())
                    .build();
            AuthUserDTO authUser = createAuthUser(createAuthUser, employee.id(), createEmployeeDTO.companyId());
            log.info(
                    "Create employee {} \n with username {} \n password {}",
                    createEmployeeDTO.firstName() + " " + createEmployeeDTO.lastName(),
                    createAuthUser.username(),
                    createAuthUser.password());

            if (createEmployeeDTO.newEmployeeInformation() != null) {
                messageAPI.sendMessage(SendMessageCommand.builder()
                        .recipientSelection(new RecipientSelection(
                                RecipientSelection.RecipientOptionType.EMPLOYEE,
                                createEmployeeDTO
                                        .newEmployeeInformation()
                                        .creatorEmployeeId()
                                        .toString()))
                        .senderEmployeeId(null) // this mean sender will be 'SYSTEM'
                        .subject(createEmployeeDTO.newEmployeeInformation().subject())
                        .description(createEmployeeDTO.newEmployeeInformation().body()
                                + System.lineSeparator()
                                + createAuthUser.username()
                                + System.lineSeparator()
                                + generatedPassword)
                        .build());
            }

            if (withPlainPassword) {
                return new CreateAuthUserResult(employee, authUser, generatedPassword);
            } else {
                return new CreateAuthUserResult(employee, authUser, null);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public AuthUserDTO createAuthUser(CreateAuthUserDTO createAuthUserDTO, UUID employeeId, UUID companyId) {
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
            ensureAuthUserHasRelationToAllOptions(authUser);
            log.info("Auth user instantiated successfully as " + authUser);
            authUserRepository.save(authUser);
            return authUserDTO(authUser);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    @Transactional
    public AuthUserDTO updateAuthUserCredentials(UpdateAuthUserDTO updateAuthUserDTO) {
        var principal = (UserPrincipal)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!passwordEncoder.matches(updateAuthUserDTO.currentPassword(), principal.getPassword())) {
            log.warn("Not allow update user credentials by user: " + principal.getEmployeeId()
                    + " - REASON: not valid password");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        try {
            AuthUser authUser = getByEmployeeId(updateAuthUserDTO.employeeId())
                    .orElseThrow(() -> new UsernameNotFoundException("Employee Id " + updateAuthUserDTO.employeeId()));

            ensureAuthUserHasRelationToAllOptions(authUser);

            if (updateAuthUserDTO.username() != null
                    && !updateAuthUserDTO.username().isEmpty()
                    && !updateAuthUserDTO.username().equals(authUser.getUsername())) {
                authUserRepository.findByUsername(updateAuthUserDTO.username()).ifPresent(existingAuthUser -> {
                    log.error("Username " + updateAuthUserDTO.username()
                            + " already exists - during update username by user: " + authUser.getUsername());
                    throw new UsernameAlreadyExistException(
                            "Username " + updateAuthUserDTO.username() + " already exists");
                });

                authUser.setUsername(updateAuthUserDTO.username());
                optionAPI.updateAuthUserOptionById(
                        new AUOID(authUser.getId(), AuthUserOptions.ALREADY_ONCE_USERNAME_MODIFICATION.getId()), true);
            }

            if (updateAuthUserDTO.password() != null
                    && !updateAuthUserDTO.password().isEmpty()) {
                authUser.setPassword(passwordEncoder.encode(updateAuthUserDTO.password()));
            }

            AuthUser saved = authUserRepository.save(authUser);

            // this object return only modified properties, consider if return full build object is required
            return AuthUserDTO.builder()
                    .employeeId(saved.getEmployeeId())
                    .username(saved.getUsername())
                    .build();
        } catch (NoSuchElementException e) {
            log.error("Update auth user failed, {}", e.getMessage());
            return null;
        }
    }

    @Override
    @Transactional
    public AuthUserDTO updateAuthUserRoles(UpdateAuthUserDTO updateAuthUserDTO) {
        try {
            AuthUser authUser = getByEmployeeId(updateAuthUserDTO.employeeId())
                    .orElseThrow(() -> new UsernameNotFoundException("Employee Id " + updateAuthUserDTO.employeeId()));

            if (!updateAuthUserDTO.roles().isEmpty()) {
                Set<Role> updatedRoles = updateAuthUserDTO.roles().stream()
                        .map(role -> roleRepository
                                .findByName(role.name())
                                .orElseThrow(() -> new NoSuchElementException("Role " + role.name() + " not found")))
                        .collect(Collectors.toSet());

                authUser.setRoles(updatedRoles);
            }

            AuthUser saved = authUserRepository.save(authUser);

            // this object return only modified properties, consider if return full build object is required
            return AuthUserDTO.builder()
                    .employeeId(saved.getEmployeeId())
                    .roles(saved.getRoles().stream()
                            .map(role -> RoleDTO.builder().name(role.getName()).build())
                            .collect(Collectors.toSet()))
                    .build();
        } catch (NoSuchElementException e) {
            log.error("Update auth user failed, {}", e.getMessage());
            return null;
        }
    }

    @Override
    @Transactional
    public AuthUserDTO updateAuthUserOptions(UpdateAuthUserDTO updateAuthUserDTO) {
        try {
            AuthUser authUser = getByEmployeeId(updateAuthUserDTO.employeeId())
                    .orElseThrow(() -> new UsernameNotFoundException("Employee Id " + updateAuthUserDTO.employeeId()));

            ensureAuthUserHasRelationToAllOptions(authUser);

            updateAuthUserDTO.options().forEach(option -> {
                optionAPI.updateAuthUserOptionById(new AUOID(authUser.getId(), option.optionId()), option.enabled());
            });

            AuthUser saved = authUserRepository.save(authUser);

            // this object return only modified properties, consider if return full build object is required
            return AuthUserDTO.builder()
                    .employeeId(saved.getEmployeeId())
                    .options(updateAuthUserDTO.options())
                    .build();
        } catch (NoSuchElementException e) {
            log.error("Update auth user failed, {}", e.getMessage());
            return null;
        }
    }

    @Transactional
    // this method has to delete AuthUser record and set false on Active column for related Employee
    public EmployeeDTO terminateEmployeeAuthUserAccount(UUID employeeId) {
        log.info("Delete authUser wit employee id: {}", employeeId);
        authUserRepository.deleteByEmployeeId(employeeId);
        log.info("Set 'active' column to 'false' for employee {}", employeeId);
        return employeeAPI.updateEmployee(UpdateEmployeeDTO.builder()
                .employeeId(employeeId)
                .active(Boolean.FALSE)
                .build());
    }

    @Transactional(readOnly = true)
    public ValidTokenResponse validateToken(ValidTokenRequest validTokenRequest) {
        try {
            var authUser = authUserRepository
                    .findByEmployeeId(jwtService.extractEmployeeId(validTokenRequest.token()))
                    .orElseThrow(() ->
                            new UsernameNotFoundException("Username " + validTokenRequest.token() + " not found"));
            var userPrincipal = new UserPrincipal(authUser);
            boolean result = jwtService.validToken(validTokenRequest.token(), userPrincipal);
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
            log.error("Login failed, {}", e.getMessage());
            throw e;
        }
    }

    private AuthResponse finalizeLoginAction(AuthUser authUser) {
        try {
            var userPrincipal = new UserPrincipal(authUser);
            String token = jwtService.generateToken(userPrincipal);
            log.info("Login successful for user: {}", authUser.getUsername());
            TenantContext.clear();
            return new AuthResponse(token);
        } finally {
            TenantContext.clear();
        }
    }

    private TenantDTO tenantDTO(Tenant tenant) {
        return TenantDTO.builder()
                .id(tenant.getId())
                .status(tenant.getStatus())
                .schemaName(tenant.getSchemaName())
                .companyId(tenant.getCompanyId())
                .build();
    }

    private AuthUserDTO authUserDTO(AuthUser authUser) {
        return AuthUserDTO.builder()
                .id(authUser.getId())
                .username(authUser.getUsername())
                .password(authUser.getPassword())
                .employeeId(authUser.getEmployeeId())
                .roles(authUser.getRoles().stream()
                        .map(role -> RoleDTO.builder().name(role.getName()).build())
                        .collect(Collectors.toSet()))
                .tenant(tenantDTO(authUser.getTenant()))
                .build();
    }

    /**
     * This method is used to related test auth users with all options, modify changelog to achieve that method can
     * be safely removed.
     * */
    void ensureAuthUserHasRelationToAllOptions(AuthUser authUser) {

        if (!authUser.getOptions()
                .contains(Option.builder()
                        .id(AuthUserOptions.AGREE_RECEIVE_SMS_NOTIFICATION.getId())
                        .build())) {
            authUser.getOptions()
                    .add(optionAPI
                            .getOptionByName(AuthUserOptions.AGREE_RECEIVE_SMS_NOTIFICATION.name())
                            .orElse(new Option()));

            optionAPI.updateAuthUserOptionById(
                    new AUOID(authUser.getId(), AuthUserOptions.AGREE_RECEIVE_SMS_NOTIFICATION.getId()), false);
        }

        if (!authUser.getOptions()
                .contains(Option.builder()
                        .id(AuthUserOptions.AGREE_RECEIVE_EMAIL_NOTIFICATION.getId())
                        .build())) {
            authUser.getOptions()
                    .add(optionAPI
                            .getOptionByName(AuthUserOptions.AGREE_RECEIVE_EMAIL_NOTIFICATION.name())
                            .orElse(new Option()));

            optionAPI.updateAuthUserOptionById(
                    new AUOID(authUser.getId(), AuthUserOptions.AGREE_RECEIVE_EMAIL_NOTIFICATION.getId()), false);
        }

        if (!authUser.getOptions()
                .contains(Option.builder()
                        .id(AuthUserOptions.ALREADY_ONCE_USERNAME_MODIFICATION.getId())
                        .build())) {
            authUser.getOptions()
                    .add(optionAPI
                            .getOptionByName(AuthUserOptions.ALREADY_ONCE_USERNAME_MODIFICATION.name())
                            .orElse(new Option()));

            optionAPI.updateAuthUserOptionById(
                    new AUOID(authUser.getId(), AuthUserOptions.ALREADY_ONCE_USERNAME_MODIFICATION.getId()), false);
        }

        entityManager.persist(authUser);
        entityManager.flush();
    }
}
