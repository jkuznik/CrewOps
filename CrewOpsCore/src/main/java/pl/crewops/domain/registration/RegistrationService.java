package pl.crewops.domain.registration;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import pl.crewops.domain.auth.AuthAPI;
import pl.crewops.domain.company.CompanyAPI;
import pl.crewops.domain.tenant.TenantAPI;
import pl.crewops.dto.CreateCustomerCommand;
import pl.crewops.dto.company.CompanyDTO;
import pl.crewops.dto.employee.CreateEmployeeDTO;
import pl.crewops.dto.tenant.TenantDTO;
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

    // TODO: implement security to allow only admin can trigger this method
    @Transactional
    TenantDTO registerCustomer(@Valid @NotNull CreateCustomerCommand createCustomerCommand) {
        String schemaName;
        try {
            schemaName = TenantSchemaNameGenerator.generateTenantSchemaName(
                    createCustomerCommand.createTenantDTO().createCompanyDTO().name(), UUID.randomUUID());
            tenantSchemaInitializer.createSchemaIfNotExists(schemaName);
            liquibaseSchemaMigrator.runMigrations(schemaName);
        } catch (CreateSchemaException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
        var notPersistedTenant = Tenant.builder().active(true).build();
        notPersistedTenant.setSchemaName(schemaName);
        notPersistedTenant.setCompanyId(UUID.randomUUID());
        var tenant = tenantAPI.saveTenant(notPersistedTenant);
        var generatedCompanyId = tenant.getCompanyId();

        TenantContext.setCurrentTenant(schemaName);
        CompanyDTO company = companyAPI.createCompany(
                createCustomerCommand.createTenantDTO().createAddressDTO(),
                createCustomerCommand.createTenantDTO().createCompanyDTO(),
                generatedCompanyId);

        var createEmployeeDTO = setCompanyId(createCustomerCommand.createEmployeeDTO(), company.id());
        authAPI.createAuthUserWithRelatedEmployee(createEmployeeDTO);
        TenantContext.clear();

        Tenant saveTenant = tenantAPI.saveTenant(tenant);
        return TenantDTO.builder()
                .id(tenant.getId())
                .companyId(saveTenant.getCompanyId())
                .active(tenant.isActive())
                .build();
    }

    private CreateEmployeeDTO setCompanyId(CreateEmployeeDTO createEmployeeDTO, UUID companyId) {
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
