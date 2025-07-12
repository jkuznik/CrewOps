package pl.crewops.domain.tenant;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pl.crewops.exception.multitenancy.TenantNotExistException;
import pl.crewops.model.publicSchema.Tenant;

@Slf4j
@Service
@RequiredArgsConstructor
class TenantService implements TenantAPI {

    private final TenantRepository tenantRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Tenant saveTenant(Tenant tenant) {
        return tenantRepository.save(tenant);
    }

    @Override
    @Transactional(readOnly = true)
    public Tenant getByCompanyId(UUID companyId) {
        return tenantRepository.findByCompanyId(companyId).orElseThrow(() -> new TenantNotExistException(companyId));
    }
}
