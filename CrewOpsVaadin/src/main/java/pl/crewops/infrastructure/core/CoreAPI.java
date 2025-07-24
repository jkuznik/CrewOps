package pl.crewops.infrastructure.core;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;
import org.springframework.validation.annotation.Validated;
import pl.crewops.auth.AuthRequest;
import pl.crewops.auth.AuthResponse;
import pl.crewops.auth.ValidTokenRequest;
import pl.crewops.auth.ValidTokenResponse;
import pl.crewops.dto.breakdown.BreakdownDTO;
import pl.crewops.dto.breakdown.CreateBreakdownDTO;
import pl.crewops.dto.breakdown.UpdateBreakdownDTO;
import pl.crewops.dto.company.CompanyDTO;
import pl.crewops.dto.employee.CreateEmployeeDTO;
import pl.crewops.dto.employee.EmployeeDTO;
import pl.crewops.dto.employee.UpdateEmployeeDTO;
import pl.crewops.dto.qualification.CreateQualificationDTO;
import pl.crewops.dto.qualification.QualificationDTO;
import pl.crewops.dto.qualification.UpdateQualificationDTO;
import pl.crewops.dto.vehicle.CreateVehicleDTO;
import pl.crewops.dto.vehicle.UpdateVehicleDTO;
import pl.crewops.dto.vehicle.VehicleDTO;
import pl.crewops.dto.vehicleType.VehicleTypeDTO;
import pl.crewops.exceptions.NotAuthenticatedException;
import pl.crewops.registration.CreateCustomerCommand;
import pl.crewops.registration.CreateCustomerResult;

@Repository
@Validated
public interface CoreAPI {

    AuthResponse login(@Valid @NotNull AuthRequest request);

    Optional<ValidTokenResponse> validateToken(@Valid @NotNull ValidTokenRequest validTokenRequest);

    Optional<EmployeeDTO> createEmployee(@Valid @NotNull CreateEmployeeDTO createEmployeeDTO)
            throws NotAuthenticatedException;

    Optional<EmployeeDTO> updateEmployee(UpdateEmployeeDTO updateEmployeeDTO) throws NotAuthenticatedException;

    Optional<QualificationDTO> createQualification(@Valid @NotNull CreateQualificationDTO createQualificationDTO)
            throws NotAuthenticatedException;

    Optional<QualificationDTO> updateQualification(@Valid @NotNull UpdateQualificationDTO updateQualificationDTO)
            throws NotAuthenticatedException;

    Optional<VehicleDTO> createVehicle(@Valid @NotNull CreateVehicleDTO createVehicleDTO)
            throws NotAuthenticatedException;

    Optional<CreateCustomerResult> registerNewCustomer(@Valid @NotNull CreateCustomerCommand command)
            throws NotAuthenticatedException;

    Optional<VehicleDTO> updateVehicle(@Valid @NotNull UpdateVehicleDTO updateVehicleDTO)
            throws NotAuthenticatedException;

    Optional<BreakdownDTO> createBreakdown(@Valid @NotNull CreateBreakdownDTO createBreakdownDTO)
            throws NotAuthenticatedException;

    Optional<BreakdownDTO> updateBreakdown(@Valid @NotNull UpdateBreakdownDTO updateBreakdownDTO)
            throws NotAuthenticatedException;

    List<EmployeeDTO> getAllEmployees() throws NotAuthenticatedException;

    Optional<EmployeeDTO> getEmployeeById(UUID employeeId) throws NotAuthenticatedException;

    List<QualificationDTO> getAllQualifications() throws NotAuthenticatedException;

    List<VehicleDTO> getAllVehicles() throws NotAuthenticatedException;

    List<VehicleTypeDTO> getAllVehicleTypes() throws NotAuthenticatedException;

    List<BreakdownDTO> getAllBreakdowns() throws NotAuthenticatedException;

    Optional<CompanyDTO> getCompanyById(@NotNull UUID companyId) throws NotAuthenticatedException;

    void deleteEmployee(@NotNull UUID employeeId) throws NotAuthenticatedException;

    void deleteQualification(@NotNull UUID qualificationId) throws NotAuthenticatedException;

    void deleteVehicle(@NotNull UUID vehicleId) throws NotAuthenticatedException;

    void setToken(@NotNull String token);

    void setAuthentication(boolean authenticated);
}
