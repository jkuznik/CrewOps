package pl.crewops.domain.tenant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import pl.crewops.domain.company.CompanyAPI;
import pl.crewops.dto.tenant.CreateTenantDTO;
import pl.crewops.dto.tenant.TenantDTO;
import pl.crewops.exception.multitenancy.CreateSchemaException;
import pl.crewops.model.publicSchema.Tenant;
import pl.crewops.utils.multitenancy.LiquibaseSchemaMigrator;
import pl.crewops.utils.multitenancy.TenantSchemaInitializer;
import pl.crewops.utils.multitenancy.TenantSchemaNameGenerator;

@SpringJUnitConfig(
        classes = {
            TenantService.class,
            TenantRepository.class,
            TenantSchemaInitializer.class,
            LiquibaseSchemaMigrator.class,
            CompanyAPI.class
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

    @MockitoBean
    CompanyAPI companyAPI;

    CreateTenantDTO createTenantDTO;
    Tenant tenant;
    String testCompanyName = "test";

    @BeforeEach
    void setUp() {
        createTenantDTO = TenantTestFactory.createTenantDTO();
        tenant = TenantTestFactory.tenant();
    }

    @Test
    void createTenant_shouldReturnTenantDTO_whenNewCustomerIsValidAndSchemaNotExists() {
        // given
        String schemaName = TenantSchemaNameGenerator.generateTenantSchemaName(testCompanyName, tenant.getId());

        // when
        when(tenantRepository.save(any(Tenant.class))).thenReturn(tenant);
        doNothing().when(tenantSchemaInitializer).createSchemaIfNotExists(schemaName);
        doNothing().when(liquibaseSchemaMigrator).runMigrations(schemaName);
        doNothing().when(companyAPI).createCompany(any(), any(), any());

        TenantDTO result = tenantService.createNewCustomer(createTenantDTO, );

        // then
        assertThat(result).isNotNull();
        assertThat(result.companyId()).isInstanceOf(UUID.class);
    }

    @Test
    void createTenant_shouldThrowException_whenNewCustomerIsAlreadyExists() {
        // given
        String schemaName = TenantSchemaNameGenerator.generateTenantSchemaName(testCompanyName, tenant.getId());

        // when
        when(tenantRepository.save(any(Tenant.class))).thenReturn(tenant);
        doThrow(new CreateSchemaException("test")).when(tenantSchemaInitializer).createSchemaIfNotExists(any());

        Exception result = catchException(() -> tenantService.createNewCustomer(createTenantDTO, ));

        // then
        assertThat(result).isInstanceOf(RuntimeException.class);
        assertThat(result.getCause()).isInstanceOf(CreateSchemaException.class);
    }
}
