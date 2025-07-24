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
import pl.crewops.domain.tenant.TenantAPI;
import pl.crewops.dto.CreateCustomerCommand;
import pl.crewops.dto.company.CompanyDTO;
import pl.crewops.dto.employee.CreateEmployeeDTO;
import pl.crewops.dto.tenant.TenantDTO;
import pl.crewops.exception.auth.RegisterCustomerException;
import pl.crewops.exception.multitenancy.CreateSchemaException;
import pl.crewops.infrastructure.multitenancy.TenantContext;
import pl.crewops.model.publicSchema.Tenant;
import pl.crewops.utils.multitenancy.LiquibaseSchemaMigrator;
import pl.crewops.utils.multitenancy.TenantSchemaInitializer;
import pl.crewops.utils.multitenancy.TenantSchemaNameGenerator;

@Slf4j
@Service
@RequiredArgsConstructor
@Validated
class RegistrationService {

    private final TenantSchemaInitializer tenantSchemaInitializer;
    private final LiquibaseSchemaMigrator liquibaseSchemaMigrator;
    private final AuthAPI authAPI;
    private final TenantAPI tenantAPI;
    private final CompanyAPI companyAPI;
    private final PlatformTransactionManager transactionManager;

    // TODO: 1 implement security to allow only admin can trigger this method
    // TODO: 2 implement logic to rollback db changes in cross schema queries in case of exception occurs for this
    // TODO: 3 make sure of nice test coverage after implement manually mantain tx rollback
    // action
    //    @Transactional
    TenantDTO registerCustomer(@Valid @NotNull CreateCustomerCommand createCustomerCommand) {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

        String schemaName = "";

        try {
            schemaName = TenantSchemaNameGenerator.generateTenantSchemaName(
                    createCustomerCommand.createTenantDTO().createCompanyDTO().name(), UUID.randomUUID());
            tenantSchemaInitializer.createSchemaIfNotExists(schemaName);
            liquibaseSchemaMigrator.runMigrations(schemaName);
        } catch (CreateSchemaException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }

        TransactionStatus saveTenantStep = transactionManager.getTransaction(def);
        UUID tenantId = null;
        UUID companyId = null;
        Tenant tenant = null;

        try {
            var notPersistedTenant = Tenant.builder().active(true).build();
            notPersistedTenant.setSchemaName(schemaName);
            notPersistedTenant.setCompanyId(UUID.randomUUID());
            tenant = tenantAPI.saveTenant(notPersistedTenant);
            transactionManager.commit(saveTenantStep);

            tenantId = tenant.getId();
            companyId = tenant.getCompanyId();
        } catch (Exception e) {
            transactionManager.rollback(saveTenantStep);
            throw new RegisterCustomerException("Failed to create tenant during registration");
        }

        TransactionStatus saveCompanyStep = transactionManager.getTransaction(def);
        CompanyDTO company = null;

        try {
            TenantContext.setCurrentTenant(schemaName);

            company = companyAPI.createCompany(
                    createCustomerCommand.createTenantDTO().createAddressDTO(),
                    createCustomerCommand.createTenantDTO().createCompanyDTO(),
                    companyId);
            transactionManager.commit(saveCompanyStep);

        } catch (Exception e) {
            transactionManager.rollback(saveCompanyStep);
            cleanTenant(tenantId);
            TenantContext.clear();
            throw new RegisterCustomerException("Failed to create company during registration");
        }

        // TODO: implement manually rollback in createAuthUserWithRelatedEmployee
        TransactionStatus saveAuthUserWithRelatedEmployee = transactionManager.getTransaction(def);

        var createEmployeeDTO =
                prepareCreateEmployeeDTOWithGeneratedCompanyId(createCustomerCommand.createEmployeeDTO(), company.id());
        try {
            authAPI.createAuthUserWithRelatedEmployee(createEmployeeDTO);
            transactionManager.commit(saveAuthUserWithRelatedEmployee);

        } catch (Exception e) {
            transactionManager.rollback(saveAuthUserWithRelatedEmployee);
            cleanCompany(companyId);
            cleanTenant(tenantId);
            throw new RegisterCustomerException("Failed to create employee during registration");
        }

        TenantContext.clear();

        return TenantDTO.builder()
                .id(tenantId)
                .companyId(companyId)
                .active(true)
                .build();
    }

    private void cleanCompany(UUID companyId) {
        companyAPI.delete(companyId);
    }

    private void cleanTenant(UUID tenantId) {
        tenantAPI.delete(tenantId);
    }

    private CreateEmployeeDTO prepareCreateEmployeeDTOWithGeneratedCompanyId(
            CreateEmployeeDTO createEmployeeDTO, UUID companyId) {
        return CreateEmployeeDTO.builder()
                .companyId(companyId)
                .firstName(createEmployeeDTO.firstName())
                .lastName(createEmployeeDTO.lastName())
                .department(createEmployeeDTO.department())
                // TODO: modify this to allow set own pass and username or implement generator mechanism (on the BE side
                //  but remember to clean DTO) - update: implement only generator mechanism but before of that have to
                //  add notifications mechanism to send generated values via email or in-app notifications and user
                //  console on frontend site to allow users modify theirs credentials
                .username(createEmployeeDTO.firstName())
                .password("pass")
                .phoneNumber(createEmployeeDTO.phoneNumber())
                .birthDate(createEmployeeDTO.birthDate())
                .roles(createEmployeeDTO.roles())
                .build();
    }
}
