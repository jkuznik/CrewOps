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
import pl.crewops.dto.qualification.CreateQualificationDTO;
import pl.crewops.dto.qualification.QualificationDTO;
import pl.crewops.dto.vehicle.CreateVehicleDTO;
import pl.crewops.dto.vehicle.VehicleDTO;

@Repository
@Validated
public interface CoreAPI {

    Optional<EmployeeDTO> createEmployee(@Valid @NotNull CreateEmployeeDTO createEmployeeDTO);

    Optional<QualificationDTO> createQualification(@Valid @NotNull CreateQualificationDTO createQualificationDTO);

    Optional<VehicleDTO> createVehicle(@Valid @NotNull CreateVehicleDTO createVehicleDTO);

    List<EmployeeDTO> getAllEmployees();

    List<QualificationDTO> getAllQualifications();

    List<VehicleDTO> getAllVehicles();

    List<QualificationDTO> getQualificationsByIds(Set<UUID> qualificationIds);

    List<VehicleDTO> getVehiclesByIds(Set<UUID> vehicleIds);

    void deleteEmployee(@NotNull UUID employeeId);

    void deleteQualification(@NotNull UUID qualificationId);

    void deleteVehicle(@NotNull UUID vehicleId);
}
