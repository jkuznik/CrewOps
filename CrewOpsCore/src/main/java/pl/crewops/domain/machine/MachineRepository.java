package pl.crewops.domain.machine;

import jakarta.validation.constraints.Size;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.crewops.model.Machine;
import pl.crewops.model.MachineType;

interface MachineRepository extends JpaRepository<Machine, UUID> {

    @EntityGraph(attributePaths = "machineType")
    Set<Machine> findAllByIdIn(Set<UUID> ids);

    @EntityGraph(attributePaths = "machineType")
    Optional<Machine> findByRegisterNumber(@Size(max = 15) String registerNumber);

    int countByMachineType(MachineType machineType);
}
