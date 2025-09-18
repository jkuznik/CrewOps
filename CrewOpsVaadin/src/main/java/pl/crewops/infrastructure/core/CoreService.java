package pl.crewops.infrastructure.core;

import java.util.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.crewops.exceptions.NotAuthenticatedException;
import pl.crewops.model.dto.auth.*;
import pl.crewops.model.dto.breakdown.BreakdownDTO;
import pl.crewops.model.dto.breakdown.CreateBreakdownDTO;
import pl.crewops.model.dto.breakdown.UpdateBreakdownDTO;
import pl.crewops.model.dto.company.CompanyDTO;
import pl.crewops.model.dto.department.DepartmentDTO;
import pl.crewops.model.dto.employee.CreateEmployeeDTO;
import pl.crewops.model.dto.employee.EmployeeDTO;
import pl.crewops.model.dto.employee.UpdateEmployeeDTO;
import pl.crewops.model.dto.machine.CreateMachineDTO;
import pl.crewops.model.dto.machine.MachineDTO;
import pl.crewops.model.dto.machine.UpdateMachineDTO;
import pl.crewops.model.dto.machineType.MachineTypeDTO;
import pl.crewops.model.dto.message.MessageDTO;
import pl.crewops.model.dto.message.SendMessageCommand;
import pl.crewops.model.dto.qualification.CreateQualificationDTO;
import pl.crewops.model.dto.qualification.QualificationDTO;
import pl.crewops.model.dto.qualification.UpdateQualificationDTO;
import pl.crewops.model.dto.qualification.UpdateQualificationExpiredAtDTO;
import pl.crewops.registration.CreateCustomerCommand;
import pl.crewops.registration.CreateCustomerResult;
import pl.crewops.security.auth.AuthRequest;
import pl.crewops.security.auth.AuthResponse;
import pl.crewops.security.auth.ValidTokenRequest;
import pl.crewops.security.auth.ValidTokenResponse;

@Slf4j
@RequiredArgsConstructor
@Service
class CoreService implements CoreAPI {

    private final CoreClient coreClient;

    @Getter
    @Setter
    private boolean authenticated;

    @Override
    public Optional<AuthResponse> login(AuthRequest request) {
        log.info("Login via service proxy");
        return Optional.ofNullable(coreClient.login(request));
    }

    @Override
    public Optional<AuthUserDTO> updateAuthUserRoles(UpdateAuthUserDTO updateAuthUserDTO)
            throws NotAuthenticatedException {
        return Optional.ofNullable(coreClient.updateAuthUserRoles(updateAuthUserDTO));
    }

    @Override
    public Optional<ValidTokenResponse> validateToken(ValidTokenRequest validTokenRequest) {
        log.debug("Validate token");
        return Optional.ofNullable(coreClient.validateToken(validTokenRequest));
    }

    @Override
    public Optional<CreateAuthUserResult> createEmployee(CreateEmployeeDTO createEmployeeDTO)
            throws NotAuthenticatedException {

        return Optional.ofNullable(coreClient.createEmployee(createEmployeeDTO));
    }

    @Override
    public Optional<EmployeeDTO> updateEmployee(UpdateEmployeeDTO updateEmployeeDTO) throws NotAuthenticatedException {

        return Optional.ofNullable(coreClient.updateEmployee(updateEmployeeDTO));
    }

    @Override
    public Optional<EmployeeDTO> addEmployeeQualification(UUID employeeId, UUID qualificationId)
            throws NotAuthenticatedException {

        return Optional.ofNullable(coreClient.addEmployeeQualification(employeeId, qualificationId));
    }

    @Override
    public Optional<QualificationDTO> createQualification(CreateQualificationDTO createQualificationDTO)
            throws NotAuthenticatedException {

        return Optional.ofNullable(coreClient.createQualification(createQualificationDTO));
    }

    @Override
    public Optional<QualificationDTO> updateQualification(UpdateQualificationDTO updateQualificationDTO)
            throws NotAuthenticatedException {

        return Optional.ofNullable(coreClient.updateQualification(updateQualificationDTO));
    }

    @Override
    public Optional<EmployeeDTO> updateQualificationExpireAt(
            UpdateQualificationExpiredAtDTO updateQualificationExpiredAtDTO) throws NotAuthenticatedException {

        return Optional.ofNullable(coreClient.updateQualificationExpireAt(updateQualificationExpiredAtDTO));
    }

    @Override
    public List<QualificationDTO> getAllQualificationsWithExpirationTimeByEmployeeId(UUID employeeId)
            throws NotAuthenticatedException {

        return coreClient.getAllQualificationsWithExpirationTimeByEmployeeId(employeeId);
    }

    @Override
    public Optional<MachineDTO> createMachine(CreateMachineDTO createMachineDTO) throws NotAuthenticatedException {

        return Optional.ofNullable(coreClient.createMachine(createMachineDTO));
    }

    @Override
    public Optional<CreateCustomerResult> registerNewCustomer(CreateCustomerCommand command) {

        return Optional.ofNullable(coreClient.registerNewCustomer(command));
    }

    @Override
    public Optional<MachineDTO> updateMachine(UpdateMachineDTO updateMachineDTO) throws NotAuthenticatedException {

        return Optional.ofNullable(coreClient.updateMachine(updateMachineDTO));
    }

    @Override
    public Optional<BreakdownDTO> createBreakdown(CreateBreakdownDTO createBreakdownDTO)
            throws NotAuthenticatedException {

        return Optional.ofNullable(coreClient.createBreakdown(createBreakdownDTO));
    }

    @Override
    public Optional<BreakdownDTO> updateBreakdown(UpdateBreakdownDTO updateBreakdownDTO)
            throws NotAuthenticatedException {

        return Optional.ofNullable(coreClient.updateBreakdown(updateBreakdownDTO));
    }

    @Override
    public List<EmployeeDTO> getAllEmployees() throws NotAuthenticatedException {
        log.info("Get all employees via service proxy");

        return coreClient.getAllEmployees();
    }

    @Override
    public Optional<EmployeeDTO> getEmployeeById(UUID employeeId) throws NotAuthenticatedException {
        log.info("Get employee by id via service proxy");
        return Optional.ofNullable(coreClient.getEmployeeById(employeeId));
    }

    @Override
    public Optional<EmployeeDTO> getEmployeeByIdNoCache(UUID employeeId) throws NotAuthenticatedException {
        log.info("Get employee by id using no cache method");
        return Optional.ofNullable(coreClient.getEmployeeByIdNoCache(employeeId));
    }

    @Override
    public List<QualificationDTO> getAllQualifications() throws NotAuthenticatedException {
        log.info("Get all qualifications via service proxy");

        return coreClient.getAllQualifications();
    }

    @Override
    public List<MachineDTO> getAllMachines() throws NotAuthenticatedException {

        return coreClient.getAllMachines();
    }

    @Override
    public List<MachineDTO> getAllEmployeeMachinesByIds(Set<UUID> ids) throws NotAuthenticatedException {

        return coreClient.getAllEmployeeMachinesByIds(ids);
    }

    @Override
    public List<MachineTypeDTO> getAllMachineTypes() throws NotAuthenticatedException {

        return coreClient.getAllMachineTypes();
    }

    @Override
    public Optional<EmployeeDTO> addEmployeeMachine(UUID employeeId, UUID machineId) throws NotAuthenticatedException {

        return Optional.ofNullable(coreClient.addEmployeeMachine(employeeId, machineId));
    }

    @Override
    public List<BreakdownDTO> getAllBreakdowns() throws NotAuthenticatedException {

        return coreClient.getAllBreakdowns();
    }

    @Override
    public List<DepartmentDTO> getAllDepartments() throws NotAuthenticatedException {
        log.info(("Get all departments via service proxy"));
        return coreClient.getAllDepartments();
    }

    @Override
    public Set<DepartmentDTO> getAllDepartmentsByIds(Set<UUID> ids) throws NotAuthenticatedException {
        log.info(("Get all departments by ids via service proxy"));
        return coreClient.getAllDepartmentsByIds(ids);
    }

    @Override
    public Optional<EmployeeDTO> addEmployeeDepartment(UUID employeeId, UUID departmentId)
            throws NotAuthenticatedException {
        return Optional.ofNullable(coreClient.addEmployeeDepartment(employeeId, departmentId));
    }

    @Override
    public Optional<CompanyDTO> getCompanyById(UUID companyId) throws NotAuthenticatedException {
        log.info("Get company by id via service proxy");

        return Optional.ofNullable(coreClient.getCompanyById(companyId));
    }

    @Override
    public List<MessageDTO> getMessagesByRecipientEmployeeId(UUID employeeId) throws NotAuthenticatedException {
        return coreClient.getMessagesByRecipientEmployeeId(employeeId);
    }

    @Override
    public Optional<MessageDTO> setMessageReadStatus(UUID messageId, boolean status) throws NotAuthenticatedException {
        return Optional.ofNullable(coreClient.setMessageReadStatus(messageId, status));
    }

    @Override
    public void sendMessage(SendMessageCommand sendMessageCommand) throws NotAuthenticatedException {
        coreClient.sendMessage(sendMessageCommand);
    }

    @Override
    public void terminateEmployeeAccount(UUID employeeId) throws NotAuthenticatedException {

        coreClient.terminateEmployeeAccount(employeeId);
    }

    @Override
    public void removeEmployeeDepartment(UUID employeeId, UUID departmentId) throws NotAuthenticatedException {
        coreClient.removeEmployeeDepartment(employeeId, departmentId);
    }

    @Override
    public void removeEmployeeQualification(UUID employeeId, UUID qualificationId) throws NotAuthenticatedException {

        coreClient.removeEmployeeQualification(employeeId, qualificationId);
    }

    @Override
    public void removeEmployeeMachine(UUID employeeId, UUID machineId) throws NotAuthenticatedException {

        coreClient.removeEmployeeMachine(employeeId, machineId);
    }

    @Override
    public void deleteQualification(UUID qualificationId) throws NotAuthenticatedException {

        coreClient.deleteQualification(qualificationId);
    }

    @Override
    public void deleteMachine(UUID vehicleId) throws NotAuthenticatedException {

        coreClient.deleteMachine(vehicleId);
    }
}
