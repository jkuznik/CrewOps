package pl.crewops.domain.tenant;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.crewops.dto.tenant.CreateTenantDTO;
import pl.crewops.dto.tenant.TenantDTO;
import pl.crewops.exception.multitenancy.CreateSchemaException;
import pl.crewops.exception.multitenancy.TenantNotExistException;
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

    @Override
    @Transactional
    public TenantDTO createTenant(CreateTenantDTO createTenantDTO) {
        var notPersistedTenant = TenantMapper.mapToEntity(createTenantDTO);
        Tenant tenant = tenantRepository.save(notPersistedTenant);

        String schemaName;
        try {
            schemaName = TenantSchemaNameGenerator.generateTenantSchemaName(tenant.getName(), tenant.getId());
            tenantSchemaInitializer.createSchemaIfNotExists(schemaName);
            liquibaseSchemaMigrator.runMigrations(schemaName);
        } catch (CreateSchemaException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }

        tenant.setSchemaName(schemaName);
        return TenantMapper.mapToDTO(tenantRepository.save(tenant));
    }

    @Override
    @Transactional
    public Tenant getByName(String name) {
        return tenantRepository.findByName(name).orElseThrow(() -> new TenantNotExistException(name));
    }
}
