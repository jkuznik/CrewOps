package pl.crewops.domain.tenant;

import static pl.crewops.domain.tenant.TenantMapper.mapToDTO;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.crewops.domain.company.CompanyAPI;
import pl.crewops.dto.tenant.CreateTenantDTO;
import pl.crewops.dto.tenant.TenantDTO;
import pl.crewops.exception.multitenancy.CreateSchemaException;
import pl.crewops.exception.multitenancy.TenantNotExistException;
import pl.crewops.infrastructure.multitenancy.TenantContext;
import pl.crewops.model.publicSchema.Tenant;
import pl.crewops.utils.multitenancy.LiquibaseSchemaMigrator;
import pl.crewops.utils.multitenancy.TenantSchemaInitializer;

@Slf4j
@Service
@RequiredArgsConstructor
class TenantService implements TenantAPI {

    private final TenantRepository tenantRepository;
    private final TenantSchemaInitializer tenantSchemaInitializer;
    private final LiquibaseSchemaMigrator liquibaseSchemaMigrator;
    private final CompanyAPI companyAPI;

    @Override
    @Transactional
    public TenantDTO createTenant(CreateTenantDTO createTenantDTO) {
        String schemaName;
        try {
            schemaName = TenantSchemaNameGenerator.generateTenantSchemaName(
                    createTenantDTO.createCompanyDTO().name(), UUID.randomUUID());
            tenantSchemaInitializer.createSchemaIfNotExists(schemaName);
            liquibaseSchemaMigrator.runMigrations(schemaName);
        } catch (CreateSchemaException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
        var notPersistedTenant = Tenant.builder().active(true).build();
        notPersistedTenant.setSchemaName(schemaName);
        notPersistedTenant.setCompanyId(UUID.randomUUID());
        var persistedTenant = tenantRepository.save(notPersistedTenant);
        var generatedCompanyId = persistedTenant.getCompanyId();

        TenantContext.setCurrentTenant(schemaName);
        companyAPI.createCompany(
                createTenantDTO.createAddressDTO(), createTenantDTO.createCompanyDTO(), generatedCompanyId);
        TenantContext.clear();

        return mapToDTO(tenantRepository.save(persistedTenant));
    }

    @Override
    @Transactional
    public Tenant getByCompanyId(UUID companyId) {
        return tenantRepository.findByCompanyId(companyId).orElseThrow(() -> new TenantNotExistException(companyId));
    }
}
