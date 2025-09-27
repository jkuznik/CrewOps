package pl.crewops.domain.registration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import pl.crewops.domain.auth.AuthAPI;
import pl.crewops.domain.company.CompanyAPI;
import pl.crewops.domain.tenant.TenantAPI;
import pl.crewops.enums.CompanyStatus;
import pl.crewops.enums.RegistrationStatus;
import pl.crewops.infrastructure.emailSender.EmailSenderAPI;
import pl.crewops.model.dto.auth.CreateAuthUserResult;
import pl.crewops.model.dto.employee.CreateEmployeeDTO;
import pl.crewops.model.dto.registration.CreateCustomerCommand;
import pl.crewops.model.dto.registration.CreateCustomerResult;
import pl.crewops.model.dto.registration.PreRegisterResponse;
import pl.crewops.model.dto.registration.VerifyEmailRequest;
import pl.crewops.model.publicSchema.Registration;
import pl.crewops.model.publicSchema.Tenant;
import pl.crewops.util.multitenancy.LiquibaseSchemaMigrator;
import pl.crewops.util.multitenancy.SchemaManager;

@SpringJUnitConfig(
        classes = {
            SchemaManager.class,
            LiquibaseSchemaMigrator.class,
            RegistrationService.class,
            RegistrationRepository.class,
            TenantAPI.class,
            AuthAPI.class,
            CompanyAPI.class,
            EmailSenderAPI.class,
            PlatformTransactionManager.class
        })
class RegistrationServiceTest {

    @Autowired
    RegistrationService registrationService;

    @MockitoBean
    SchemaManager schemaManager;

    @MockitoBean
    LiquibaseSchemaMigrator liquibaseSchemaMigrator;

    @MockitoBean
    RegistrationRepository registrationRepository;

    @MockitoBean
    TenantAPI tenantAPI;

    @MockitoBean
    AuthAPI authAPI;

    @MockitoBean
    CompanyAPI companyAPI;

    @MockitoBean
    EmailSenderAPI emailSenderAPI;

    @MockitoBean
    PlatformTransactionManager transactionManager;

    @Autowired
    ApplicationContext applicationContext;

    private CreateCustomerCommand validCommand;

    @BeforeEach
    void setUp() {
        validCommand = RegistrationTestFactory.createCustomerCommand();
    }

    @Test
    void registerCustomer_shouldReturnPendingRegistration() {
        Registration savedRegistration = Registration.builder()
                .status(RegistrationStatus.PENDING)
                .verificationCode(12345)
                .companyName(validCommand.createTenantDTO().createCompanyDTO().name())
                .taxId(validCommand.createTenantDTO().createCompanyDTO().taxId())
                .email(validCommand.createTenantDTO().createCompanyDTO().email())
                .city(validCommand.createTenantDTO().createAddressDTO().city())
                .postalCode(validCommand.createTenantDTO().createAddressDTO().postalCode())
                .street(validCommand.createTenantDTO().createAddressDTO().street())
                .localNumber(validCommand.createTenantDTO().createAddressDTO().localNumber())
                .firstName(validCommand.createEmployeeDTO().firstName())
                .lastName(validCommand.createEmployeeDTO().lastName())
                .birthDate(validCommand
                        .createEmployeeDTO()
                        .birthDate()
                        .atStartOfDay(ZoneId.systemDefault())
                        .toInstant())
                .build();

        savedRegistration.setId(UUID.randomUUID());

        when(registrationRepository.save(any())).thenReturn(savedRegistration);

        PreRegisterResponse response = registrationService.registerCustomer(validCommand);

        assertThat(response).isNotNull();
        assertThat(response.registrationId()).isEqualTo(savedRegistration.getId());
        assertThat(response.code()).isEqualTo(PreRegisterResponse.PreRegisterResponseCode.EMAIL_VERIFICATION_REQUIRED);

        verify(emailSenderAPI).sendEmail(any());
    }

    @Test
    void finalizeRegisterCustomer_shouldCreateTenantCompanyEmployee() {
        // Prepare pending registration
        Registration pendingRegistration = Registration.builder()
                .status(RegistrationStatus.PENDING)
                .verificationCode(12345)
                .companyName(validCommand.createTenantDTO().createCompanyDTO().name())
                .taxId(validCommand.createTenantDTO().createCompanyDTO().taxId())
                .email(validCommand.createTenantDTO().createCompanyDTO().email())
                .city(validCommand.createTenantDTO().createAddressDTO().city())
                .postalCode(validCommand.createTenantDTO().createAddressDTO().postalCode())
                .street(validCommand.createTenantDTO().createAddressDTO().street())
                .localNumber(validCommand.createTenantDTO().createAddressDTO().localNumber())
                .firstName(validCommand.createEmployeeDTO().firstName())
                .lastName(validCommand.createEmployeeDTO().lastName())
                .birthDate(validCommand
                        .createEmployeeDTO()
                        .birthDate()
                        .atStartOfDay(ZoneId.systemDefault())
                        .toInstant())
                .build();
        pendingRegistration.setId(UUID.randomUUID());

        when(registrationRepository.findById(pendingRegistration.getId())).thenReturn(Optional.of(pendingRegistration));

        // Mock tenant
        Tenant mockTenant = Tenant.builder()
                .companyId(UUID.randomUUID())
                .status(CompanyStatus.ACTIVE)
                .build();
        mockTenant.setId(UUID.randomUUID());
        when(tenantAPI.saveTenant(any())).thenReturn(mockTenant);

        // Mock company
        pl.crewops.model.dto.company.CompanyDTO mockCompanyDTO = mock(pl.crewops.model.dto.company.CompanyDTO.class);
        when(companyAPI.createCompany(any(), any(), any())).thenReturn(mockCompanyDTO);

        // Mock auth user result
        CreateAuthUserResult mockAuthUserResult = mock(CreateAuthUserResult.class);
        var mockAuthUserDTO = mock(pl.crewops.model.dto.auth.AuthUserDTO.class);
        when(mockAuthUserDTO.username()).thenReturn("user123");
        when(mockAuthUserResult.authUserDTO()).thenReturn(mockAuthUserDTO);
        when(mockAuthUserResult.plainPassword()).thenReturn("password123");
        when(authAPI.createAuthUserWithRelatedEmployeeForRegisterCustomerRequirements(any(CreateEmployeeDTO.class)))
                .thenReturn(mockAuthUserResult);

        // Mock transaction manager
        TransactionStatus mockTransactionStatus = mock(TransactionStatus.class);
        when(transactionManager.getTransaction(any(DefaultTransactionDefinition.class)))
                .thenReturn(mockTransactionStatus);

        // Execute
        VerifyEmailRequest request = new VerifyEmailRequest(
                pendingRegistration.getId(),
                String.valueOf(pendingRegistration.getVerificationCode()),
                "subject",
                "body");

        CreateCustomerResult result = registrationService.finalizeRegisterCustomer(request);

        // Verify
        assertThat(result).isNotNull();
        assertThat(result.authUserResult()).isEqualTo(mockAuthUserResult);
        assertThat(result.companyDTO()).isEqualTo(mockCompanyDTO);

        // Ensure email sent
        verify(emailSenderAPI).sendEmail(any());
        // Ensure registration status updated
        verify(registrationRepository).save(pendingRegistration);
    }

    @Test
    void finalizeRegisterCustomer_shouldThrowException_whenRegistrationNotFound() {
        UUID randomId = UUID.randomUUID();
        when(registrationRepository.findById(randomId)).thenReturn(Optional.empty());

        VerifyEmailRequest request = new VerifyEmailRequest(randomId, String.valueOf(12345), "subject", "body");

        assertThatThrownBy(() -> registrationService.finalizeRegisterCustomer(request))
                .isInstanceOf(java.util.NoSuchElementException.class);
    }

    @Test
    void expireOldRegistrations_shouldCallRepository() {
        Instant threshold = Instant.now().minusSeconds(600);
        when(registrationRepository.expirePendingRegistrations(any())).thenReturn(2);

        registrationService.expireOldRegistrations();

        verify(registrationRepository).expirePendingRegistrations(any());
    }
}
