package pl.crewops.domain.registration;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.validation.annotation.Validated;
import pl.crewops.domain.auth.AuthAPI;
import pl.crewops.domain.company.CompanyAPI;
import pl.crewops.domain.tenant.TenantAPI;
import pl.crewops.enums.CompanyStatus;
import pl.crewops.enums.RegistrationStatus;
import pl.crewops.exception.domain.company.NoUniqueCompanyTaxIdException;
import pl.crewops.exception.domain.registration.NotFoundPendingRegistrationException;
import pl.crewops.exception.domain.registration.RegisterCustomerException;
import pl.crewops.exception.multitenancy.CreateSchemaException;
import pl.crewops.infrastructure.emailSender.EmailSenderAPI;
import pl.crewops.infrastructure.emailSender.SendEmailRequest;
import pl.crewops.infrastructure.multitenancy.TenantContext;
import pl.crewops.model.auth.RoleType;
import pl.crewops.model.dto.address.CreateAddressDTO;
import pl.crewops.model.dto.auth.CreateAuthUserResult;
import pl.crewops.model.dto.auth.RoleDTO;
import pl.crewops.model.dto.company.CompanyDTO;
import pl.crewops.model.dto.company.CreateCompanyDTO;
import pl.crewops.model.dto.employee.CreateEmployeeDTO;
import pl.crewops.model.dto.registration.CreateCustomerCommand;
import pl.crewops.model.dto.registration.CreateCustomerResult;
import pl.crewops.model.dto.registration.PreRegisterResponse;
import pl.crewops.model.dto.registration.VerifyEmailRequest;
import pl.crewops.model.publicSchema.Registration;
import pl.crewops.model.publicSchema.Tenant;
import pl.crewops.util.multitenancy.LiquibaseSchemaMigrator;
import pl.crewops.util.multitenancy.SchemaManager;
import pl.crewops.util.multitenancy.TenantSchemaNameGenerator;

@Slf4j
@Service
@RequiredArgsConstructor
@Validated
class RegistrationService {

    private final SchemaManager schemaManager;
    private final LiquibaseSchemaMigrator liquibaseSchemaMigrator;

    private final RegistrationRepository registrationRepository;
    private final AuthAPI authAPI;
    private final TenantAPI tenantAPI;
    private final CompanyAPI companyAPI;
    private final EmailSenderAPI emailSenderAPI;
    private final PlatformTransactionManager transactionManager;

    PreRegisterResponse registerCustomer(@Valid @NotNull CreateCustomerCommand createCustomerCommand) {
        var taxId = createCustomerCommand.createTenantDTO().createCompanyDTO().taxId();
        if (tenantAPI.getOptionalByTaxId(taxId).isPresent()) {
            return new PreRegisterResponse(null, PreRegisterResponse.PreRegisterResponseCode.TAX_ID_ALREADY_EXIST);
        }

        RandomUtils secure = RandomUtils.secure();
        int verificationCode = secure.randomInt(10000, 99999);

        var pendingRegistration = buildPendingRegistrationRecord(createCustomerCommand, verificationCode);

        Registration save = registrationRepository.save(pendingRegistration);

        final String VERIFICATION_CODE = "CrewOps System – Company Registration Verification Code";

        emailSenderAPI.sendEmail(SendEmailRequest.builder()
                .toEmailAddress(createCustomerCommand
                        .createTenantDTO()
                        .createCompanyDTO()
                        .email())
                .subject(VERIFICATION_CODE)
                .body(String.valueOf(verificationCode))
                .build());

        return new PreRegisterResponse(
                save.getId(), PreRegisterResponse.PreRegisterResponseCode.EMAIL_VERIFICATION_REQUIRED);
    }

    private Registration buildPendingRegistrationRecord(
            CreateCustomerCommand createCustomerCommand, int verificationCode) {
        return Registration.builder()
                .status(RegistrationStatus.PENDING)
                .verificationCode(verificationCode)
                .companyName(createCustomerCommand
                        .createTenantDTO()
                        .createCompanyDTO()
                        .name())
                .taxId(createCustomerCommand
                        .createTenantDTO()
                        .createCompanyDTO()
                        .taxId())
                .email(createCustomerCommand
                        .createTenantDTO()
                        .createCompanyDTO()
                        .email())
                .postalCode(createCustomerCommand
                        .createTenantDTO()
                        .createAddressDTO()
                        .postalCode())
                .city(createCustomerCommand.createTenantDTO().createAddressDTO().city())
                .street(createCustomerCommand
                        .createTenantDTO()
                        .createAddressDTO()
                        .street())
                .localNumber(createCustomerCommand
                        .createTenantDTO()
                        .createAddressDTO()
                        .localNumber())
                .firstName(createCustomerCommand.createEmployeeDTO().firstName())
                .lastName(createCustomerCommand.createEmployeeDTO().lastName())
                .phoneNumber(createCustomerCommand.createEmployeeDTO().phoneNumber())
                .birthDate(createCustomerCommand
                        .createEmployeeDTO()
                        .birthDate() // <-- LocalDate
                        .atStartOfDay(ZoneId.systemDefault()) // LocalDateTime
                        .toInstant())
                .build();
    }

    CreateCustomerResult finalizeRegisterCustomer(@Valid @NotNull VerifyEmailRequest request) {
        Registration registration = registrationRepository
                .findById(request.registrationId())
                .orElseThrow(() -> new NotFoundPendingRegistrationException(request.registrationId()));

        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

        CreateAuthUserResult authUserWithRelatedEmployee;
        CompanyDTO companyDTO;

        String schemaName;

        try {
            schemaName = TenantSchemaNameGenerator.generateTenantSchemaName(
                    registration.getCompanyName(), UUID.randomUUID());
            schemaManager.createSchemaIfNotExists(schemaName);
            liquibaseSchemaMigrator.runMigrations(schemaName);
        } catch (CreateSchemaException e) {
            log.error(e.getMessage());
            throw new CreateSchemaException("Fail to create schema during customer registration.\n" + e.getMessage());
        }

        TransactionStatus saveTenantStep = transactionManager.getTransaction(def);
        UUID tenantId;
        UUID companyId;
        Tenant tenant;

        try {
            var notPersistedTenant =
                    Tenant.builder().status(CompanyStatus.TRIAL).build();
            notPersistedTenant.setSchemaName(schemaName);
            notPersistedTenant.setCompanyId(UUID.randomUUID());
            notPersistedTenant.setTaxId(registration.getTaxId());
            tenant = tenantAPI.saveTenant(notPersistedTenant);
            transactionManager.commit(saveTenantStep);

            tenantId = tenant.getId();
            companyId = tenant.getCompanyId();
        } catch (NoUniqueCompanyTaxIdException e) {
            transactionManager.rollback(saveTenantStep);
            schemaManager.dropSchema(schemaName);
            throw new RegisterCustomerException("Failed to create tenant during registration");
        }

        TransactionStatus saveCompanyStep = transactionManager.getTransaction(def);

        try {
            TenantContext.setCurrentTenant(schemaName);

            companyDTO = companyAPI.createCompany(
                    CreateAddressDTO.builder()
                            .city(registration.getCity())
                            .postalCode(registration.getPostalCode())
                            .street(registration.getStreet())
                            .localNumber(registration.getLocalNumber())
                            .build(),
                    CreateCompanyDTO.builder()
                            .email(registration.getEmail())
                            .taxId(registration.getTaxId())
                            .name(registration.getCompanyName())
                            .build(),
                    companyId);
            transactionManager.commit(saveCompanyStep);

        } catch (Exception e) {
            transactionManager.rollback(saveCompanyStep);
            cleanTenant(tenantId);
            TenantContext.clear();
            schemaManager.dropSchema(schemaName);
            throw new RegisterCustomerException("Failed to create company during registration");
        }

        TransactionStatus saveAuthUserWithRelatedEmployee = transactionManager.getTransaction(def);

        var updatedCreateEmployeeDto = updateCompanyId(
                CreateEmployeeDTO.builder()
                        .firstName(registration.getFirstName())
                        .lastName(registration.getLastName())
                        .birthDate(LocalDate.ofInstant(registration.getBirthDate(), ZoneId.systemDefault()))
                        .phoneNumber(registration.getPhoneNumber())
                        .roles(Set.of(
                                RoleDTO.builder()
                                        .name(RoleType.COMPANY_ADMIN.name())
                                        .build(),
                                RoleDTO.builder().name(RoleType.EMPLOYEE.name()).build()))
                        .build(),
                companyDTO.id());
        try {
            authUserWithRelatedEmployee =
                    authAPI.createAuthUserWithRelatedEmployeeForRegisterCustomerRequirements(updatedCreateEmployeeDto);

            transactionManager.commit(saveAuthUserWithRelatedEmployee);

            log.info("Register new employee successfully");
            registration.setStatus(RegistrationStatus.SUCCESS);
            registrationRepository.save(registration);

            String subject = request.subject();
            String bodyTemplate = request.body();

            String body = bodyTemplate
                    .replace("{0}", authUserWithRelatedEmployee.authUserDTO().username())
                    .replace("{1}", authUserWithRelatedEmployee.plainPassword())
                    .replace("%n", System.lineSeparator());

            var sendEmailRequest = SendEmailRequest.builder()
                    .toEmailAddress(registration.getEmail())
                    .subject(subject)
                    .body(body)
                    .build();

            emailSenderAPI.sendEmail(sendEmailRequest);

        } catch (Exception e) {
            transactionManager.rollback(saveAuthUserWithRelatedEmployee);
            cleanTenant(tenantId);
            TenantContext.clear();
            schemaManager.dropSchema(schemaName);
            throw new RegisterCustomerException("Failed to create employee during registration");
        }

        TenantContext.clear();

        return new CreateCustomerResult(authUserWithRelatedEmployee, companyDTO);
    }

    private void cleanTenant(UUID tenantId) {
        tenantAPI.delete(tenantId);
    }

    private CreateEmployeeDTO updateCompanyId(CreateEmployeeDTO createEmployeeDTO, UUID companyId) {
        return CreateEmployeeDTO.builder()
                .companyId(companyId)
                .firstName(createEmployeeDTO.firstName())
                .lastName(createEmployeeDTO.lastName())
                .departments(createEmployeeDTO.departments())
                .phoneNumber(createEmployeeDTO.phoneNumber())
                .birthDate(createEmployeeDTO.birthDate())
                .roles(createEmployeeDTO.roles())
                .build();
    }

    @Scheduled(cron = "0 0 3 * * *", zone = "Europe/Warsaw")
    @Transactional
    public void expireOldRegistrations() {
        Instant threshold = Instant.now().minus(10, ChronoUnit.MINUTES);
        int updated = registrationRepository.expirePendingRegistrations(threshold);
        if (updated > 0) {
            log.info("Expired " + updated + " registrations");
        }
    }
}
