package pl.crewops.domain.address;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.crewops.model.tenantSchema.Address;

interface AddressRepository extends JpaRepository<Address, UUID> {}
