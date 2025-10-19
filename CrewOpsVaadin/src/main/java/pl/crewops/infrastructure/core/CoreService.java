package pl.crewops.infrastructure.core;

import static pl.crewops.util.CacheResolver.*;
import static pl.crewops.util.CacheResolver.GET_ALL_BREAKDOWNS;
import static pl.crewops.util.CacheResolver.GET_ALL_MACHINES;
import static pl.crewops.util.CacheResolver.GET_ALL_MACHINE_TYPES;
import static pl.crewops.util.CacheResolver.GET_ALL_QUALIFICATIONS;
import static pl.crewops.util.CacheResolver.GET_ALL_QUALIFICATIONS_WITH_EXPIRATION_TIME;

import java.time.LocalDate;
import java.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import pl.crewops.exceptions.NotAuthenticatedException;
import pl.crewops.model.dto.auth.*;
import pl.crewops.model.dto.auth.AuthRequest;
import pl.crewops.model.dto.auth.AuthResponse;
import pl.crewops.model.dto.breakdown.BreakdownDTO;
import pl.crewops.model.dto.breakdown.CreateBreakdownDTO;
import pl.crewops.model.dto.breakdown.UpdateBreakdownDTO;
import pl.crewops.model.dto.company.CompanyDTO;
import pl.crewops.model.dto.dailyEntry.CreateDailyEntryDTO;
import pl.crewops.model.dto.dailyEntry.DailyEntryDTO;
import pl.crewops.model.dto.dailyEntry.UpdateDailyEntryCommand;
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
import pl.crewops.model.dto.option.AuthUserOptionDTO;
import pl.crewops.model.dto.qualification.CreateQualificationDTO;
import pl.crewops.model.dto.qualification.QualificationDTO;
import pl.crewops.model.dto.qualification.UpdateQualificationDTO;
import pl.crewops.model.dto.qualification.UpdateQualificationExpiredAtDTO;
import pl.crewops.model.dto.registration.CreateCustomerCommand;
import pl.crewops.model.dto.registration.CreateCustomerResult;
import pl.crewops.model.dto.registration.PreRegisterResponse;
import pl.crewops.model.dto.registration.VerifyEmailRequest;

@Slf4j
@Service
class CoreService implements CoreAPI {

    private final CoreClient coreClient;

    private final AuthClient authClient;
    private final EmployeeClient employeeClient;

    public CoreService(CoreClient coreClient) {
        this.coreClient = coreClient;
        this.authClient = new AuthClient(coreClient.getCoreClient(), coreClient.getAuthorizationProvider());
        this.employeeClient = new EmployeeClient(coreClient.getAuthorizationProvider());
    }

    @Override
    public Optional<CreateCustomerResult> verifyEmail(VerifyEmailRequest request) {
        return Optional.ofNullable(authClient.verifyEmail(request));
    }

    @Override
    public Optional<PreRegisterResponse> registerNewCustomer(CreateCustomerCommand command) {
        return Optional.ofNullable(authClient.registerNewCustomer(command));
    }

    @Caching(
            evict = {
                @CacheEvict(value = GET_EMPLOYEE_BY_ID, allEntries = true),
                @CacheEvict(value = GET_COMPANY_BY_ID, allEntries = true),
                @CacheEvict(value = GET_ALL_EMPLOYEES, allEntries = true),
                @CacheEvict(value = GET_ALL_QUALIFICATIONS, allEntries = true),
                @CacheEvict(value = GET_ALL_QUALIFICATIONS_WITH_EXPIRATION_TIME, allEntries = true),
                @CacheEvict(value = GET_ALL_BREAKDOWNS, allEntries = true),
                @CacheEvict(value = GET_ALL_MACHINES, allEntries = true),
                @CacheEvict(value = GET_ALL_MACHINE_TYPES, allEntries = true),
            })
    @Override
    public Optional<AuthResponse> login(AuthRequest request) {
        return Optional.ofNullable(authClient.login(request));
    }

    @Caching(
            evict = {
                @CacheEvict(value = GET_EMPLOYEE_BY_ID, key = "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()"),
                @CacheEvict(value = GET_COMPANY_BY_ID, key = "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()")
            })
    @Override
    public Optional<AuthUserDTO> updateAuthUserCredentials(UpdateAuthUserDTO updateAuthUserDTO)
            throws NotAuthenticatedException {
        return Optional.ofNullable(authClient.updateAuthUserCredentials(updateAuthUserDTO));
    }

    @Caching(
            evict = {
                @CacheEvict(value = GET_EMPLOYEE_BY_ID, key = "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()"),
                @CacheEvict(value = GET_COMPANY_BY_ID, key = "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()")
            })
    @Override
    public Optional<AuthUserDTO> updateAuthUserRoles(UpdateAuthUserDTO updateAuthUserDTO)
            throws NotAuthenticatedException {
        return Optional.ofNullable(authClient.updateAuthUserRoles(updateAuthUserDTO));
    }

    @Caching(
            evict = {
                @CacheEvict(value = GET_ALL_EMPLOYEES, key = "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()"),
                @CacheEvict(
                        value = GET_ALL_QUALIFICATIONS,
                        key = "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()")
            })
    @Override
    public void terminateEmployeeAccount(UUID employeeId) throws NotAuthenticatedException {
        authClient.terminateEmployeeAccount(employeeId);
    }

    @Caching(
            evict = {
                @CacheEvict(value = GET_EMPLOYEE_BY_ID, key = "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()"),
                @CacheEvict(value = GET_ALL_EMPLOYEES, key = "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()")
            })
    @Override
    public Optional<CreateAuthUserResult> createEmployee(CreateEmployeeDTO createEmployeeDTO)
            throws NotAuthenticatedException {
        return Optional.ofNullable(employeeClient.createEmployee(createEmployeeDTO));
    }

    @Cacheable(cacheNames = GET_ALL_EMPLOYEES, key = "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()")
    @Override
    public List<EmployeeDTO> getAllEmployees() throws NotAuthenticatedException {
        log.info("Cache Miss - getAllEmployees");
        return employeeClient.getAllEmployees();
    }

    @Cacheable(cacheNames = GET_EMPLOYEE_BY_ID, key = "#employeeId")
    @Override
    public Optional<EmployeeDTO> getEmployeeById(UUID employeeId) throws NotAuthenticatedException {
        log.info("Cache Miss - getEmployeeById");
        return Optional.ofNullable(employeeClient.getEmployeeById(employeeId));
    }

    @Caching(
            evict = {
                @CacheEvict(value = GET_EMPLOYEE_BY_ID, key = "#updateEmployeeDTO.employeeId"),
                @CacheEvict(
                        value = GET_ALL_QUALIFICATIONS,
                        key = "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()"),
                @CacheEvict(
                        value = GET_ALL_QUALIFICATIONS_WITH_EXPIRATION_TIME,
                        key = "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()"),
                @CacheEvict(value = GET_ALL_EMPLOYEES, key = "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()")
            })
    @Override
    public Optional<EmployeeDTO> updateEmployee(UpdateEmployeeDTO updateEmployeeDTO) throws NotAuthenticatedException {
        return Optional.ofNullable(employeeClient.updateEmployee(updateEmployeeDTO));
    }

    @Caching(
            evict = {
                @CacheEvict(value = GET_EMPLOYEE_BY_ID, key = "#updateEmployeeDTO.employeeId"),
                @CacheEvict(
                        value = GET_ALL_QUALIFICATIONS,
                        key = "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()"),
                @CacheEvict(
                        value = GET_ALL_QUALIFICATIONS_WITH_EXPIRATION_TIME,
                        key = "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()"),
                @CacheEvict(value = GET_ALL_EMPLOYEES, key = "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()")
            })
    @Override
    public Optional<EmployeeDTO> updateEmployeeSelfProfile(UpdateEmployeeDTO updateEmployeeDTO)
            throws NotAuthenticatedException {

        return Optional.ofNullable(employeeClient.updateEmployeeSelfProfile(updateEmployeeDTO));
    }

    // TODO: cache evict logic
    @Override
    public Optional<EmployeeDTO> addEmployeeDepartment(UUID employeeId, UUID departmentId)
            throws NotAuthenticatedException {
        return Optional.ofNullable(employeeClient.addEmployeeDepartment(employeeId, departmentId));
    }

    @Caching(
            evict = {
                @CacheEvict(value = GET_ALL_BREAKDOWNS, key = "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()"),
                @CacheEvict(value = GET_ALL_MACHINES, key = "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()"),
                @CacheEvict(
                        value = GET_ALL_MACHINES_BY_IDS,
                        key = "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()"),
                @CacheEvict(
                        value = GET_ALL_MACHINE_TYPES,
                        key = "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()")
            })
    @Override
    public Optional<EmployeeDTO> addEmployeeMachine(UUID employeeId, UUID machineId) throws NotAuthenticatedException {
        return Optional.ofNullable(employeeClient.addEmployeeMachine(employeeId, machineId));
    }

    @Caching(
            evict = {
                @CacheEvict(value = GET_EMPLOYEE_BY_ID, key = "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()"),
                @CacheEvict(
                        value = GET_ALL_QUALIFICATIONS,
                        key = "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()"),
                @CacheEvict(
                        value = GET_ALL_QUALIFICATIONS_WITH_EXPIRATION_TIME,
                        key = "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()")
            })
    @Override
    public Optional<EmployeeDTO> addEmployeeQualification(UUID employeeId, UUID qualificationId)
            throws NotAuthenticatedException {
        return Optional.ofNullable(employeeClient.addEmployeeQualification(employeeId, qualificationId));
    }

    // TODO: cache evict logic
    @Override
    public void removeEmployeeDepartment(UUID employeeId, UUID departmentId) throws NotAuthenticatedException {
        employeeClient.removeEmployeeDepartment(employeeId, departmentId);
    }

    @Caching(
            evict = {
                @CacheEvict(value = GET_ALL_MACHINES, key = "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()"),
                @CacheEvict(value = GET_ALL_EMPLOYEES, key = "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()")
            })
    @Override
    public void removeEmployeeMachine(UUID employeeId, UUID machineId) throws NotAuthenticatedException {
        employeeClient.removeEmployeeMachine(employeeId, machineId);
    }

    @Caching(
            evict = {
                @CacheEvict(
                        value = GET_ALL_QUALIFICATIONS,
                        key = "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()"),
                @CacheEvict(
                        value = GET_ALL_QUALIFICATIONS_WITH_EXPIRATION_TIME,
                        key = "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()"),
                @CacheEvict(value = GET_ALL_EMPLOYEES, key = "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()")
            })
    @Override
    public void removeEmployeeQualification(UUID employeeId, UUID qualificationId) throws NotAuthenticatedException {
        employeeClient.removeEmployeeQualification(employeeId, qualificationId);
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
    public Optional<DailyEntryDTO> createDailyEntry(CreateDailyEntryDTO createDailyEntryDTO)
            throws NotAuthenticatedException {
        return Optional.ofNullable(coreClient.createDailyEntry(createDailyEntryDTO));
    }

    @Override
    public Optional<DailyEntryDTO> findDailyEntryByEmployeeIdAndDate(UUID employeeId, LocalDate localDate)
            throws NotAuthenticatedException {
        return Optional.ofNullable(coreClient.findDailyEntryByEmployeeIdAndDate(employeeId, localDate));
    }

    @Override
    public Optional<DailyEntryDTO> updateDailyEntrySelfPemission(UpdateDailyEntryCommand updateDailyEntryCommand)
            throws NotAuthenticatedException {
        return Optional.ofNullable(coreClient.updateDailyEntrySelfPermission(updateDailyEntryCommand));
    }

    @Override
    public Optional<MachineDTO> createMachine(CreateMachineDTO createMachineDTO) throws NotAuthenticatedException {

        return Optional.ofNullable(coreClient.createMachine(createMachineDTO));
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
    public List<QualificationDTO> getAllQualifications() throws NotAuthenticatedException {
        log.info("Cache Miss - getALlQualifications");
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
    public List<BreakdownDTO> getAllBreakdowns() throws NotAuthenticatedException {

        return coreClient.getAllBreakdowns();
    }

    @Override
    public List<DepartmentDTO> getAllDepartments() throws NotAuthenticatedException {
        log.info(("Get all departments via service proxy"));
        return coreClient.getAllDepartments();
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
    public void deleteQualification(UUID qualificationId) throws NotAuthenticatedException {

        coreClient.deleteQualification(qualificationId);
    }

    @Override
    public Set<AuthUserOptionDTO> getOptionsByEmployeeId(UUID employeeId) throws NotAuthenticatedException {
        return coreClient.getOptionsByEmployeeId(employeeId);
    }

    @Override
    public void deleteMachine(UUID vehicleId) throws NotAuthenticatedException {

        coreClient.deleteMachine(vehicleId);
    }
}
