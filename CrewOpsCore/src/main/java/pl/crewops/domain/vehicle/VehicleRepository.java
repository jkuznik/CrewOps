package pl.crewops.domain.vehicle;

import jakarta.validation.constraints.Size;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.crewops.model.Vehicle;
import pl.crewops.model.VehicleType;

interface VehicleRepository extends JpaRepository<Vehicle, UUID> {

    Set<Vehicle> findAllByIdIn(Set<UUID> ids);

    Optional<Vehicle> findByRegisterNumber(@Size(max = 15) String registerNumber);

    int countByVehicleType(VehicleType vehicleType);
}
