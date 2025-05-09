package pl.crewops.domain.breakdown;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.crewops.model.Breakdown;

@Repository
interface BreakdownRepository extends JpaRepository<Breakdown, UUID> {}
