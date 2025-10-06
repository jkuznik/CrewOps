package pl.crewops.domain.machineType;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.crewops.model.tenantSchema.MachineType;

interface MachineTypeRepository extends JpaRepository<MachineType, UUID> {

    Optional<MachineType> findByName(String name);
}
