package pl.crewops.domain.vehicleType;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.crewops.model.VehicleType;

interface VehicleTypeRepository extends JpaRepository<VehicleType, UUID> {

    Optional<VehicleType> findByName(String name);
}
