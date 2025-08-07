package pl.crewops.infrastructure.core;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
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
import pl.crewops.dto.machine.CreateMachineDTO;
import pl.crewops.dto.machine.MachineDTO;
import pl.crewops.dto.machine.UpdateMachineDTO;
import pl.crewops.dto.machineType.MachineTypeDTO;
import pl.crewops.dto.qualification.CreateQualificationDTO;
import pl.crewops.dto.qualification.QualificationDTO;
import pl.crewops.dto.qualification.UpdateQualificationDTO;
import pl.crewops.dto.qualification.UpdateQualificationExpiredAtDTO;
import pl.crewops.exceptions.NotAuthenticatedException;
import pl.crewops.registration.CreateCustomerCommand;
import pl.crewops.registration.CreateCustomerResult;

@Slf4j
@RequiredArgsConstructor
@Service
class CoreService implements CoreAPI {

    private final CoreClient coreClient;

    @Getter
    @Setter
    private boolean authenticated;

    @Override
    public AuthResponse login(AuthRequest request) {
        log.info("Login via service proxy");
        return coreClient.login(request);
    }

    @Override
    public Optional<ValidTokenResponse> validateToken(ValidTokenRequest validTokenRequest) {
        log.debug("Validate token");
        return coreClient.validateToken(validTokenRequest);
    }

    @Override
    public Optional<EmployeeDTO> createEmployee(CreateEmployeeDTO createEmployeeDTO) throws NotAuthenticatedException {
        isAuthenticated();
        return Optional.ofNullable(coreClient.createEmployee(createEmployeeDTO));
    }

    @Override
    public Optional<EmployeeDTO> updateEmployee(UpdateEmployeeDTO updateEmployeeDTO) throws NotAuthenticatedException {
        isAuthenticated();
        return Optional.ofNullable(coreClient.updateEmployee(updateEmployeeDTO));
    }

    @Override
    public Optional<EmployeeDTO> addEmployeeQualification(UUID employeeId, UUID qualificationId)
            throws NotAuthenticatedException {
        isAuthenticated();
        return Optional.ofNullable(coreClient.addEmployeeQualification(employeeId, qualificationId));
    }

    @Override
    public Optional<QualificationDTO> createQualification(CreateQualificationDTO createQualificationDTO)
            throws NotAuthenticatedException {
        isAuthenticated();
        return Optional.ofNullable(coreClient.createQualification(createQualificationDTO));
    }

    @Override
    public Optional<QualificationDTO> updateQualification(UpdateQualificationDTO updateQualificationDTO)
            throws NotAuthenticatedException {
        isAuthenticated();
        return Optional.ofNullable(coreClient.updateQualification(updateQualificationDTO));
    }

    @Override
    public Optional<EmployeeDTO> updateQualificationExpireAt(
            UpdateQualificationExpiredAtDTO updateQualificationExpiredAtDTO) throws NotAuthenticatedException {
        isAuthenticated();
        return Optional.ofNullable(coreClient.updateQualificationExpireAt(updateQualificationExpiredAtDTO));
    }

    @Override
    public List<QualificationDTO> getAllQualificationsWithExpirationTimeByEmployeeId(UUID employeeId)
            throws NotAuthenticatedException {
        isAuthenticated();
        return coreClient.getAllQualificationsWithExpirationTimeByEmployeeId(employeeId);
    }

    @Override
    public Optional<MachineDTO> createMachine(CreateMachineDTO createMachineDTO) throws NotAuthenticatedException {
        isAuthenticated();
        return Optional.ofNullable(coreClient.createMachine(createMachineDTO));
    }

    @Override
    public Optional<CreateCustomerResult> registerNewCustomer(CreateCustomerCommand command)
            throws NotAuthenticatedException {
        isAuthenticated();
        return Optional.ofNullable(coreClient.registerNewCustomer(command));
    }

    @Override
    public Optional<MachineDTO> updateMachine(UpdateMachineDTO updateMachineDTO) throws NotAuthenticatedException {
        isAuthenticated();
        return Optional.ofNullable(coreClient.updateMachine(updateMachineDTO));
    }

    @Override
    public Optional<BreakdownDTO> createBreakdown(CreateBreakdownDTO createBreakdownDTO)
            throws NotAuthenticatedException {
        isAuthenticated();
        return Optional.ofNullable(coreClient.createBreakdown(createBreakdownDTO));
    }

    @Override
    public Optional<BreakdownDTO> updateBreakdown(UpdateBreakdownDTO updateBreakdownDTO)
            throws NotAuthenticatedException {
        isAuthenticated();
        return Optional.ofNullable(coreClient.updateBreakdown(updateBreakdownDTO));
    }

    @Override
    public List<EmployeeDTO> getAllEmployees() throws NotAuthenticatedException {
        log.info("Get all employees via service proxy");
        isAuthenticated();
        return coreClient.getAllEmployees();
    }

    @Override
    public Optional<EmployeeDTO> getEmployeeById(UUID employeeId) throws NotAuthenticatedException {
        log.info("Get employee by id via service proxy");
        isAuthenticated();
        return Optional.ofNullable(coreClient.getEmployeeById(employeeId));
    }

    @Override
    public List<QualificationDTO> getAllQualifications() throws NotAuthenticatedException {
        log.info("Get all qualifications via service proxy");
        isAuthenticated();
        return coreClient.getAllQualifications();
    }

    @Override
    public List<MachineDTO> getAllMachines() throws NotAuthenticatedException {
        isAuthenticated();
        return coreClient.getAllMachines();
    }

    @Override
    public List<MachineTypeDTO> getAllMachineTypes() throws NotAuthenticatedException {
        isAuthenticated();
        return coreClient.getAllMachineTypes();
    }

    @Override
    public List<BreakdownDTO> getAllBreakdowns() throws NotAuthenticatedException {
        isAuthenticated();
        return coreClient.getAllBreakdowns();
    }

    @Override
    public Optional<CompanyDTO> getCompanyById(UUID companyId) throws NotAuthenticatedException {
        log.info("Get company by id via service proxy");
        isAuthenticated();
        return Optional.ofNullable(coreClient.getCompanyById(companyId));
    }

    @Override
    public void terminateEmployeeAccount(UUID employeeId) throws NotAuthenticatedException {
        isAuthenticated();
        coreClient.terminateEmployeeAccount(employeeId);
    }

    @Override
    public void removeEmployeeQualification(UUID employeeId, UUID qualificationId) throws NotAuthenticatedException {
        isAuthenticated();
        coreClient.removeEmployeeQualification(employeeId, qualificationId);
    }

    @Override
    public void deleteQualification(UUID qualificationId) throws NotAuthenticatedException {
        isAuthenticated();
        coreClient.deleteQualification(qualificationId);
    }

    @Override
    public void deleteMachine(UUID vehicleId) throws NotAuthenticatedException {
        isAuthenticated();
        coreClient.deleteMachine(vehicleId);
    }

    @Override
    public void setToken(String token) {
        coreClient.setToken(token);
    }

    @Override
    public void setAuthentication(boolean authenticated) {
        this.authenticated = authenticated;
    }

    private void isAuthenticated() throws NotAuthenticatedException {
        log.debug("Checking authentication");
        if (!authenticated) {
            log.error("Authentication failed");
            throw new NotAuthenticatedException();
        }
    }
}
