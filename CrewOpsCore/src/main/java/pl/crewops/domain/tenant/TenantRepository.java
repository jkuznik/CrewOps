package pl.crewops.domain.tenant;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.crewops.model.publicSchema.Tenant;

interface TenantRepository extends JpaRepository<Tenant, UUID> {

    Optional<Tenant> findByCompanyId(UUID companyId);
}
