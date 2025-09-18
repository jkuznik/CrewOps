package pl.crewops.domain.tenant;

import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pl.crewops.exception.domain.company.NoUniqueCompanyTaxIdException;
import pl.crewops.exception.multitenancy.TenantNotExistException;
import pl.crewops.model.publicSchema.Tenant;

@Slf4j
@Service
@RequiredArgsConstructor
class TenantService implements TenantAPI {

    private final TenantRepository tenantRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    // this propagation is required to force commit and achieve access to tenant_id generated on db side after INSERT
    // query
    public Tenant saveTenant(Tenant tenant) {
        if (tenantRepository.findByTaxId(tenant.getTaxId()).isPresent()) {
            throw new NoUniqueCompanyTaxIdException(tenant.getTaxId());
        }
        return tenantRepository.save(tenant);
    }

    @Override
    @Transactional(readOnly = true)
    public Tenant getByCompanyId(UUID companyId) {
        return tenantRepository.findByCompanyId(companyId).orElseThrow(() -> new TenantNotExistException(companyId));
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Tenant> getOptionalByTaxId(String taxId) {
        return tenantRepository.findByTaxId(taxId);
    }

    @Override
    @Transactional
    public void delete(UUID tenantId) {
        Tenant tenant = tenantRepository.findById(tenantId).orElseThrow(() -> new TenantNotExistException(tenantId));
        tenantRepository.delete(tenant);
    }
}
