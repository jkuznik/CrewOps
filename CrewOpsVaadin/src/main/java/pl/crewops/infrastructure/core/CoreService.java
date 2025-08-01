package pl.crewops.infrastructure.core;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.crewops.dto.auth.AuthRequest;
import pl.crewops.dto.auth.AuthResponse;
import pl.crewops.dto.auth.ValidTokenRequest;
import pl.crewops.dto.auth.ValidTokenResponse;
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

@Slf4j
@RequiredArgsConstructor
@Service
class CoreService implements CoreAPI {

    private final CoreClient coreClient;

    @Override
    public AuthResponse login(AuthRequest request) {
        log.info("Login via service proxy");
        return coreClient.login(request);
    }

    @Override
    public Optional<ValidTokenResponse> validateToken(ValidTokenRequest validTokenRequest) {
        return coreClient.validateToken(validTokenRequest);
    }

    @Override
    public Optional<EmployeeDTO> createEmployee(CreateEmployeeDTO createEmployeeDTO) throws NotAuthenticatedException {
        return coreClient.createEmployee(createEmployeeDTO);
    }

    @Override
    public Optional<EmployeeDTO> updateEmployee(UpdateEmployeeDTO updateEmployeeDTO) throws NotAuthenticatedException {
        return coreClient.updateEmployee(updateEmployeeDTO);
    }

    @Override
    public Optional<QualificationDTO> createQualification(CreateQualificationDTO createQualificationDTO)
            throws NotAuthenticatedException {
        return coreClient.createQualification(createQualificationDTO);
    }

    @Override
    public Optional<QualificationDTO> updateQualification(UpdateQualificationDTO updateQualificationDTO)
            throws NotAuthenticatedException {
        return coreClient.updateQualification(updateQualificationDTO);
    }

    @Override
    public Optional<VehicleDTO> createVehicle(CreateVehicleDTO createVehicleDTO) throws NotAuthenticatedException {
        return coreClient.createVehicle(createVehicleDTO);
    }

    @Override
    public Optional<CreateCustomerResult> registerNewCustomer(CreateCustomerCommand command)
            throws NotAuthenticatedException {
        return coreClient.registerNewCustomer(command);
    }

    @Override
    public Optional<VehicleDTO> updateVehicle(UpdateVehicleDTO updateVehicleDTO) throws NotAuthenticatedException {
        return coreClient.updateVehicle(updateVehicleDTO);
    }

    @Override
    public Optional<BreakdownDTO> createBreakdown(CreateBreakdownDTO createBreakdownDTO)
            throws NotAuthenticatedException {
        return coreClient.createBreakdown(createBreakdownDTO);
    }

    @Override
    public Optional<BreakdownDTO> updateBreakdown(UpdateBreakdownDTO updateBreakdownDTO)
            throws NotAuthenticatedException {
        return coreClient.updateBreakdown(updateBreakdownDTO);
    }

    @Override
    public List<EmployeeDTO> getAllEmployees() throws NotAuthenticatedException {
        return coreClient.getAllEmployees();
    }

    @Override
    public Optional<EmployeeDTO> getEmployeeById(UUID employeeId) throws NotAuthenticatedException {
        return coreClient.getEmployeeById(employeeId);
    }

    @Override
    public List<QualificationDTO> getAllQualifications() throws NotAuthenticatedException {
        return coreClient.getAllQualifications();
    }

    @Override
    public List<VehicleDTO> getAllVehicles() throws NotAuthenticatedException {
        return coreClient.getAllVehicles();
    }

    @Override
    public List<VehicleTypeDTO> getAllVehicleTypes() throws NotAuthenticatedException {
        return coreClient.getAllVehicleTypes();
    }

    @Override
    public List<BreakdownDTO> getAllBreakdowns() throws NotAuthenticatedException {
        return coreClient.getAllBreakdowns();
    }

    @Override
    public Optional<CompanyDTO> getCompanyById(UUID companyId) throws NotAuthenticatedException {
        return coreClient.getCompanyById(companyId);
    }

    @Override
    public void terminateEmployeeAccount(UUID employeeId) throws NotAuthenticatedException {
        coreClient.terminateEmployeeAccount(employeeId);
    }

    @Override
    public void deleteQualification(UUID qualificationId) throws NotAuthenticatedException {
        coreClient.deleteQualification(qualificationId);
    }

    @Override
    public void deleteVehicle(UUID vehicleId) throws NotAuthenticatedException {
        coreClient.deleteVehicle(vehicleId);
    }

    @Override
    public void setToken(String token) {
        coreClient.setToken(token);
    }

    @Override
    public void setAuthentication(boolean authenticated) {
        coreClient.setAuthentication(authenticated);
    }
}
