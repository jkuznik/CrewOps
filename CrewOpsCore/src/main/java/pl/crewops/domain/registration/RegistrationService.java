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

    @Transactional
    Void registerCustomer(@Valid @NotNull CreateCustomerCommand createCustomerCommand) {
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

        // TODO: clean this solution after PoC !!!! extra ugly solution
        // TODO do it directly after successful implement registration feature
        var createEmployeeDTO = extractCreateEmployeeDTO(createCustomerCommand.createEmployeeDTO(), company.id());
        authAPI.createEmployee(createEmployeeDTO);
        TenantContext.clear();

        tenantAPI.saveTenant(tenant);
        return null;
    }

    private CreateEmployeeDTO extractCreateEmployeeDTO(@NotNull CreateEmployeeDTO employeeDTO, UUID companyId) {
        return CreateEmployeeDTO.builder()
                .companyId(
                        companyId) // keep attention! this valid is set only to satisfy constraints validations - in be
                // logic companyId is token other way
                .firstName(employeeDTO.firstName())
                .lastName(employeeDTO.lastName())
                .department(employeeDTO.department())
                // TODO: modify this to allow set own pass and username or implement generate mechanism (on the BE side
                // but remind to clean DTO)
                .username(employeeDTO.firstName())
                .password("pass")
                .phoneNumber(employeeDTO.phoneNumber())
                .birthDate(employeeDTO.birthDate())
                .roles(employeeDTO.roles())
                .build();
    }
}
