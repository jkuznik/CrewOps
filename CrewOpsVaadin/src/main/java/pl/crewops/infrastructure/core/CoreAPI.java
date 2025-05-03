package pl.crewops.infrastructure.core;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.stereotype.Repository;
import org.springframework.validation.annotation.Validated;
import pl.crewops.auth.AuthRequest;
import pl.crewops.auth.AuthResponse;
import pl.crewops.auth.ValidTokenRequest;
import pl.crewops.auth.ValidTokenResponse;
import pl.crewops.dto.employee.CreateEmployeeDTO;
import pl.crewops.dto.employee.EmployeeDTO;
import pl.crewops.dto.qualification.CreateQualificationDTO;
import pl.crewops.dto.qualification.QualificationDTO;
import pl.crewops.dto.vehicle.CreateVehicleDTO;
import pl.crewops.dto.vehicle.VehicleDTO;
import pl.crewops.exceptions.NotAuthenticatedException;

@Repository
@Validated
public interface CoreAPI {

    AuthResponse login(@Valid @NotNull AuthRequest request);

    ValidTokenResponse validateToken(@Valid @NotNull ValidTokenRequest validTokenRequest);

    Optional<EmployeeDTO> createEmployee(@Valid @NotNull CreateEmployeeDTO createEmployeeDTO)
            throws NotAuthenticatedException;

    Optional<QualificationDTO> createQualification(@Valid @NotNull CreateQualificationDTO createQualificationDTO)
            throws NotAuthenticatedException;

    Optional<VehicleDTO> createVehicle(@Valid @NotNull CreateVehicleDTO createVehicleDTO)
            throws NotAuthenticatedException;

    List<EmployeeDTO> getAllEmployees() throws NotAuthenticatedException;

    List<QualificationDTO> getAllQualifications() throws NotAuthenticatedException;

    List<VehicleDTO> getAllVehicles() throws NotAuthenticatedException;

    List<QualificationDTO> getQualificationsByIds(Set<UUID> qualificationIds) throws NotAuthenticatedException;

    List<VehicleDTO> getVehiclesByIds(Set<UUID> vehicleIds) throws NotAuthenticatedException;

    void deleteEmployee(@NotNull UUID employeeId) throws NotAuthenticatedException;

    void deleteQualification(@NotNull UUID qualificationId) throws NotAuthenticatedException;

    void deleteVehicle(@NotNull UUID vehicleId) throws NotAuthenticatedException;

    void setToken(@Valid @NotNull AuthResponse response);

    void resetToken();
}
