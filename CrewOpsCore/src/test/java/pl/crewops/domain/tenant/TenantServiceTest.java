package pl.crewops.domain.tenant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import pl.crewops.exception.domain.company.NoUniqueCompanyTaxIdException;
import pl.crewops.exception.multitenancy.TenantNotExistException;
import pl.crewops.model.publicSchema.Tenant;

@SpringJUnitConfig(classes = {TenantService.class, TenantRepository.class})
class TenantServiceTest {

    @Autowired
    TenantService tenantService;

    @MockitoBean
    TenantRepository tenantRepository;

    private final UUID tenantId = UUID.randomUUID();
    private final UUID companyId = UUID.randomUUID();
    private final String taxId = "1234567890";

    private Tenant buildTenant() {
        Tenant tenant = new Tenant();
        tenant.setId(tenantId);
        tenant.setCompanyId(companyId);
        tenant.setTaxId(taxId);
        return tenant;
    }

    @Test
    void saveTenant_ShouldReturnTenant_WhenTaxIdIsUnique() {
        // given
        Tenant tenant = buildTenant();
        when(tenantRepository.findByTaxId(taxId)).thenReturn(Optional.empty());
        when(tenantRepository.save(tenant)).thenReturn(tenant);

        // when
        Tenant result = tenantService.saveTenant(tenant);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getTaxId()).isEqualTo(taxId);
        verify(tenantRepository).save(tenant);
    }

    @Test
    void saveTenant_ShouldThrowException_WhenTaxIdIsNotUnique() {
        // given
        Tenant tenant = buildTenant();
        when(tenantRepository.findByTaxId(taxId)).thenReturn(Optional.of(tenant));

        // when
        Exception exception = catchException(() -> tenantService.saveTenant(tenant));

        // then
        assertThat(exception).isExactlyInstanceOf(NoUniqueCompanyTaxIdException.class);
        verify(tenantRepository, never()).save(any());
    }

    @Test
    void getByCompanyId_ShouldReturnTenant_WhenExists() {
        // given
        Tenant tenant = buildTenant();
        when(tenantRepository.findByCompanyId(companyId)).thenReturn(Optional.of(tenant));

        // when
        Tenant result = tenantService.getByCompanyId(companyId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getCompanyId()).isEqualTo(companyId);
    }

    @Test
    void getByCompanyId_ShouldThrowException_WhenNotExists() {
        // given
        when(tenantRepository.findByCompanyId(companyId)).thenReturn(Optional.empty());

        // when
        Exception exception = catchException(() -> tenantService.getByCompanyId(companyId));

        // then
        assertThat(exception).isExactlyInstanceOf(TenantNotExistException.class);
    }

    @Test
    void getOptionalByTaxId_ShouldReturnTenant_WhenExists() {
        // given
        Tenant tenant = buildTenant();
        when(tenantRepository.findByTaxId(taxId)).thenReturn(Optional.of(tenant));

        // when
        Optional<Tenant> result = tenantService.getOptionalByTaxId(taxId);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getTaxId()).isEqualTo(taxId);
    }

    @Test
    void getOptionalByTaxId_ShouldReturnEmpty_WhenNotExists() {
        // given
        when(tenantRepository.findByTaxId(taxId)).thenReturn(Optional.empty());

        // when
        Optional<Tenant> result = tenantService.getOptionalByTaxId(taxId);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    void delete_ShouldDeleteTenant_WhenExists() {
        // given
        Tenant tenant = buildTenant();
        when(tenantRepository.findById(tenantId)).thenReturn(Optional.of(tenant));

        // when
        tenantService.delete(tenantId);

        // then
        verify(tenantRepository).delete(tenant);
    }

    @Test
    void delete_ShouldThrowException_WhenNotExists() {
        // given
        when(tenantRepository.findById(tenantId)).thenReturn(Optional.empty());

        // when
        Exception exception = catchException(() -> tenantService.delete(tenantId));

        // then
        assertThat(exception).isExactlyInstanceOf(TenantNotExistException.class);
        verify(tenantRepository, never()).delete(any());
    }
}
