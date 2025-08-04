package pl.crewops.domain.breakdown;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.crewops.model.Breakdown;
import pl.crewops.model.Machine;

interface BreakdownRepository extends JpaRepository<Breakdown, UUID> {

    Optional<Breakdown> findFirstByMachineAndSolvedIsFalse(Machine machine);
}
