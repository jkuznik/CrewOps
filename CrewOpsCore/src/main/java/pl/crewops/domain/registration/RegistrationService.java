package pl.crewops.domain.registration;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.validation.annotation.Validated;
import pl.crewops.domain.auth.AuthAPI;
import pl.crewops.domain.company.CompanyAPI;
import pl.crewops.domain.message.MessageAPI;
import pl.crewops.domain.tenant.TenantAPI;
import pl.crewops.dto.auth.CreateAuthUserResult;
import pl.crewops.dto.company.CompanyDTO;
import pl.crewops.dto.employee.CreateEmployeeDTO;
import pl.crewops.dto.message.RecipientSelection;
import pl.crewops.dto.message.SendMessageCommand;
import pl.crewops.enums.CompanyStatus;
import pl.crewops.exception.domain.company.NoUniqueCompanyTaxIdException;
import pl.crewops.exception.domain.registration.RegisterCustomerException;
import pl.crewops.exception.multitenancy.CreateSchemaException;
import pl.crewops.infrastructure.multitenancy.TenantContext;
import pl.crewops.model.publicSchema.Tenant;
import pl.crewops.registration.CreateCustomerCommand;
import pl.crewops.registration.CreateCustomerResult;
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
    private final AuthAPI authAPI;
    private final TenantAPI tenantAPI;
    private final CompanyAPI companyAPI;
    private final MessageAPI messageAPI;
    private final PlatformTransactionManager transactionManager;

    CreateCustomerResult registerCustomer(@Valid @NotNull CreateCustomerCommand createCustomerCommand) {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

        CreateAuthUserResult authUserWithRelatedEmployee;
        CompanyDTO companyDTO;

        String schemaName;

        try {
            schemaName = TenantSchemaNameGenerator.generateTenantSchemaName(
                    createCustomerCommand.createTenantDTO().createCompanyDTO().name(), UUID.randomUUID());
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
                    Tenant.builder().status(CompanyStatus.ACTIVE).build();
            notPersistedTenant.setSchemaName(schemaName);
            notPersistedTenant.setCompanyId(UUID.randomUUID());
            notPersistedTenant.setTaxId(
                    createCustomerCommand.createTenantDTO().createCompanyDTO().taxId());
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
                    createCustomerCommand.createTenantDTO().createAddressDTO(),
                    createCustomerCommand.createTenantDTO().createCompanyDTO(),
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

        var updatedCreateEmployeeDto = updateCompanyId(createCustomerCommand.createEmployeeDTO(), companyDTO.id());
        try {
            authUserWithRelatedEmployee =
                    authAPI.createAuthUserWithRelatedEmployeeForRegisterCustomer(updatedCreateEmployeeDto);

            var sendMessageCommand = SendMessageCommand.builder()
                    // todo: modify this to departments option with value of system admin departments will be
                    .recipientSelection(new RecipientSelection(RecipientSelection.RecipientOptionType.ALL, null))
                    .title(createCustomerCommand
                            .createTenantDTO()
                            .createCompanyDTO()
                            .name())
                    .description("Login: "
                            + authUserWithRelatedEmployee.authUserDTO().username() + " Pass: "
                            + authUserWithRelatedEmployee.plainPassword())
                    .senderEmployeeId(null)
                    .build();

            log.info("Register new employee successfully");

            //            messageAPI.sendMessage(sendMessageCommand);

            log.info("Send message successfully");
            transactionManager.commit(saveAuthUserWithRelatedEmployee);
        } catch (Exception e) {
            transactionManager.rollback(saveAuthUserWithRelatedEmployee);
            cleanTenant(tenantId);
            cleanCompany(companyId, schemaName);
            TenantContext.clear();
            schemaManager.dropSchema(schemaName);
            throw new RegisterCustomerException("Failed to create employee during registration");
        }

        TenantContext.clear();

        return new CreateCustomerResult(authUserWithRelatedEmployee, companyDTO);
    }

    private void cleanCompany(UUID companyId, String schemaName) {
        companyAPI.deleteAfterFailedCustomerRegister(companyId, schemaName);
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
}
