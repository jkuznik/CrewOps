package pl.crewops.domain.tenant;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.crewops.model.publicSchema.Tenant;

interface TenantRepository extends JpaRepository<Tenant, UUID> {}
