package pl.crewops.infrastructure.core;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.stereotype.Repository;
import org.springframework.validation.annotation.Validated;
import pl.crewops.dto.employee.CreateEmployeeDTO;
import pl.crewops.dto.employee.EmployeeDTO;
import pl.crewops.dto.qualification.QualificationDTO;
import pl.crewops.dto.vehicle.VehicleDTO;

@Repository
@Validated
public interface CoreAPI {

    Optional<EmployeeDTO> createEmployee(@Valid @NotNull CreateEmployeeDTO createEmployeeDTO);

    List<EmployeeDTO> getEmployees();

    List<QualificationDTO> getQualifications(Set<UUID> qualificationIds);

    List<VehicleDTO> getVehicles(Set<UUID> vehicleIds);
}
