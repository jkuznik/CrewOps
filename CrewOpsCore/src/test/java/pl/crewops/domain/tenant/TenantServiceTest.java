package pl.crewops.domain.tenant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import pl.crewops.dto.tenant.CreateTenantDTO;
import pl.crewops.dto.tenant.TenantDTO;
import pl.crewops.exception.multitenancy.CreateSchemaException;
import pl.crewops.model.publicSchema.Tenant;
import pl.crewops.utils.multitenancy.LiquibaseSchemaMigrator;
import pl.crewops.utils.multitenancy.TenantSchemaInitializer;

@SpringJUnitConfig(
        classes = {
            TenantService.class,
            TenantRepository.class,
            TenantSchemaInitializer.class,
            LiquibaseSchemaMigrator.class
        })
class TenantServiceTest {

    @Autowired
    TenantService tenantService;

    @MockitoBean
    TenantRepository tenantRepository;

    @MockitoBean
    TenantSchemaInitializer tenantSchemaInitializer;

    @MockitoBean
    LiquibaseSchemaMigrator liquibaseSchemaMigrator;

    CreateTenantDTO createTenantDTO;
    Tenant tenant;

    @BeforeEach
    void setUp() {
        createTenantDTO = TenantTestFactory.createTenantDTO();
        tenant = TenantTestFactory.tenant();
    }

    @Test
    void createTenant_shouldReturnTenantDTO_whenTenantIsValidAndSchemaNotExists() {
        // given
        String schemaName = TenantSchemaNameGenerator.generateTenantSchemaName(tenant.getName(), tenant.getId());

        // when
        when(tenantRepository.save(any(Tenant.class))).thenReturn(tenant);
        doNothing().when(tenantSchemaInitializer).createSchemaIfNotExists(schemaName);
        doNothing().when(liquibaseSchemaMigrator).runMigrations(schemaName);

        TenantDTO result = tenantService.createTenant(createTenantDTO);

        // then
        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("test");
    }

    @Test
    void createTenant_shouldThrowException_whenTenantIsAlreadyExists() {
        // given
        String schemaName = TenantSchemaNameGenerator.generateTenantSchemaName(tenant.getName(), tenant.getId());

        // when
        when(tenantRepository.save(any(Tenant.class))).thenReturn(tenant);
        doThrow(new CreateSchemaException("test")).when(tenantSchemaInitializer).createSchemaIfNotExists(any());

        Exception result = catchException(() -> tenantService.createTenant(createTenantDTO));

        // then
        assertThat(result).isInstanceOf(RuntimeException.class);
        assertThat(result.getCause()).isInstanceOf(CreateSchemaException.class);
    }
}
