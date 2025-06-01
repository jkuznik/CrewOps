package pl.crewops.domain.breakdown;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.crewops.model.Breakdown;
import pl.crewops.model.Vehicle;

@Repository
interface BreakdownRepository extends JpaRepository<Breakdown, UUID> {

    Optional<Breakdown> findFirstByVehicleAndSolvedIsFalse(Vehicle vehicle);
}
