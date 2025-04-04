package pl.kuznik.domain.vehicle;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.kuznik.entity.Vehicle;

@Repository
interface VehicleRepository extends JpaRepository<Vehicle, UUID> {}
