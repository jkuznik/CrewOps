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
import pl.crewops.model.dto.jobPosition.CreateJobPositionDTO;
import pl.crewops.model.dto.jobPosition.JobPositionDTO;
import pl.crewops.model.dto.jobPosition.UpdateJobPositionDTO;
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

    private final DomainAuthClient domainAuthClient;
    private final DomainBreakdownClient domainBreakdownClient;
    private final DomainCompanyClient domainCompanyClient;
    private final DomainDailyClient domainDailyClient;
    private final DomainDepartmentClient domainDepartmentClient;
    private final DomainJobPositionClient domainJobPositionClient;
    private final DomainEmployeeClient domainEmployeeClient;
    private final DomainMachineClient domainMachineClient;
    private final DomainMessageClient domainMessageClient;
    private final DomainOptionClient domainOptionClient;
    private final DomainQualificationClient deleteQualificationClient;

    public CoreService(CoreClient coreClient) {
        this.domainAuthClient = new DomainAuthClient(coreClient.getAuthorizationProvider(), coreClient.getCoreClient());
        this.domainBreakdownClient = new DomainBreakdownClient(coreClient.getAuthorizationProvider());
        this.domainCompanyClient = new DomainCompanyClient(coreClient.getAuthorizationProvider());
        this.domainDailyClient = new DomainDailyClient(coreClient.getAuthorizationProvider());
        this.domainDepartmentClient = new DomainDepartmentClient(coreClient.getAuthorizationProvider());
        this.domainJobPositionClient = new DomainJobPositionClient(coreClient.getAuthorizationProvider());
        this.domainEmployeeClient = new DomainEmployeeClient(coreClient.getAuthorizationProvider());
        this.domainMachineClient = new DomainMachineClient(coreClient.getAuthorizationProvider());
        this.domainMessageClient = new DomainMessageClient(coreClient.getAuthorizationProvider());
        this.domainOptionClient = new DomainOptionClient(coreClient.getAuthorizationProvider());
        this.deleteQualificationClient = new DomainQualificationClient(coreClient.getAuthorizationProvider());
    }

    // --- DOMAIN AUTH CLIENT (Rejestracja, Logowanie, Aktualizacje Autoryzacji) ---

    @Override
    public Optional<PreRegisterResponse> registerNewCustomer(CreateCustomerCommand command) {
        return Optional.ofNullable(domainAuthClient.registerNewCustomer(command));
    }

    @Override
    public Optional<CreateCustomerResult> verifyEmail(VerifyEmailRequest request) {
        return Optional.ofNullable(domainAuthClient.verifyEmail(request));
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
        return Optional.ofNullable(domainAuthClient.login(request));
    }

    @Caching(
            evict = {
                @CacheEvict(value = GET_EMPLOYEE_BY_ID, key = "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()"),
                @CacheEvict(value = GET_COMPANY_BY_ID, key = "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()")
            })
    @Override
    public Optional<AuthUserDTO> updateAuthUserCredentials(UpdateAuthUserDTO updateAuthUserDTO)
            throws NotAuthenticatedException {
        return Optional.ofNullable(domainAuthClient.updateAuthUserCredentials(updateAuthUserDTO));
    }

    @Caching(
            evict = {
                @CacheEvict(value = GET_EMPLOYEE_BY_ID, key = "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()"),
                @CacheEvict(value = GET_COMPANY_BY_ID, key = "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()")
            })
    @Override
    public Optional<AuthUserDTO> updateAuthUserRoles(UpdateAuthUserDTO updateAuthUserDTO)
            throws NotAuthenticatedException {
        return Optional.ofNullable(domainAuthClient.updateAuthUserRoles(updateAuthUserDTO));
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
        domainAuthClient.terminateEmployeeAccount(employeeId);
    }

    // --- DOMAIN BREAKDOWN CLIENT (Awarie) ---

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
    public Optional<BreakdownDTO> createBreakdown(CreateBreakdownDTO createBreakdownDTO)
            throws NotAuthenticatedException {

        return Optional.ofNullable(domainBreakdownClient.createBreakdown(createBreakdownDTO));
    }

    @Cacheable(cacheNames = GET_ALL_BREAKDOWNS, key = "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()")
    @Override
    public List<BreakdownDTO> getAllBreakdowns() throws NotAuthenticatedException {

        return domainBreakdownClient.getAllBreakdowns();
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
    public Optional<BreakdownDTO> updateBreakdown(UpdateBreakdownDTO updateBreakdownDTO)
            throws NotAuthenticatedException {
        return Optional.ofNullable(domainBreakdownClient.updateBreakdown(updateBreakdownDTO));
    }

    // --- DOMAIN COMPANY CLIENT (Firma) ---

    @Cacheable(cacheNames = GET_COMPANY_BY_ID, key = "#companyId")
    @Override
    public Optional<CompanyDTO> getCompanyById(UUID companyId) throws NotAuthenticatedException {
        return Optional.ofNullable(domainCompanyClient.getCompanyById(companyId));
    }

    // --- DOMAIN DAILY CLIENT (Raporty Dzienne) ---

    @CacheEvict(
            cacheNames = GET_DAILY_ENTRY_BY_EMPLOYEE_AND_DATE,
            key = "#createDailyEntryDTO.employeeId() + '_' + #createDailyEntryDTO.entryDate()")
    @Override
    public Optional<DailyEntryDTO> createDailyEntry(CreateDailyEntryDTO createDailyEntryDTO)
            throws NotAuthenticatedException {
        return Optional.ofNullable(domainDailyClient.createDailyEntry(createDailyEntryDTO));
    }

    @Cacheable(cacheNames = GET_DAILY_ENTRY_BY_EMPLOYEE_AND_DATE, key = "#employeeId + '_' + #localDate")
    @Override
    public Optional<DailyEntryDTO> findDailyEntryByEmployeeIdAndDate(UUID employeeId, LocalDate localDate)
            throws NotAuthenticatedException {
        return Optional.ofNullable(domainDailyClient.findDailyEntryByEmployeeIdAndDate(employeeId, localDate));
    }

    @CacheEvict(
            cacheNames = GET_DAILY_ENTRY_BY_EMPLOYEE_AND_DATE,
            key = "#updateDailyEntryCommand.employeeId() + '_' + #updateDailyEntryCommand.entryDate()")
    @Override
    public Optional<DailyEntryDTO> updateDailyEntrySelfPermission(UpdateDailyEntryCommand updateDailyEntryCommand)
            throws NotAuthenticatedException {
        return Optional.ofNullable(domainDailyClient.updateDailyEntrySelfPermission(updateDailyEntryCommand));
    }

    @CacheEvict(
            cacheNames = GET_DAILY_ENTRY_BY_EMPLOYEE_AND_DATE,
            key = "#updateDailyEntryCommand.employeeId() + '_' + #updateDailyEntryCommand.entryDate()")
    @Override
    public Optional<DailyEntryDTO> approveDailyEntry(UpdateDailyEntryCommand updateDailyEntryCommand)
            throws NotAuthenticatedException {
        return Optional.ofNullable(domainDailyClient.approveDailyEntry(updateDailyEntryCommand));
    }

    // --- DOMAIN DEPARTMENT CLIENT (Działy) ---

    @Cacheable(cacheNames = GET_ALL_DEPARTMENTS, key = "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()")
    @Override
    public List<DepartmentDTO> getAllDepartments() throws NotAuthenticatedException {
        log.info(("Get all departments via service proxy"));
        return domainDepartmentClient.getAllDepartments();
    }

    // --- DOMAIN JOB POSITION CLIENT (Stanowiska Pracy) ---

    @Override
    public JobPositionDTO createJobPosition(CreateJobPositionDTO createJobPositionDTO)
            throws NotAuthenticatedException {
        return domainJobPositionClient.createJobPosition(createJobPositionDTO);
    }

    @Override
    public List<JobPositionDTO> getAllJobPositions() throws NotAuthenticatedException {
        return domainJobPositionClient.getAllJobPositions();
    }

    @Override
    public JobPositionDTO updateJobPosition(UpdateJobPositionDTO updateJobPositionDTO)
            throws NotAuthenticatedException {
        return domainJobPositionClient.updateJobPosition(updateJobPositionDTO);
    }

    @Override
    public void deleteById(UUID id) throws NotAuthenticatedException {
        domainJobPositionClient.deleteById(id);
    }

    // --- DOMAIN EMPLOYEE CLIENT (Pracownicy) ---

    @Caching(
            evict = {
                @CacheEvict(value = GET_EMPLOYEE_BY_ID, key = "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()"),
                @CacheEvict(value = GET_ALL_EMPLOYEES, key = "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()")
            })
    @Override
    public Optional<CreateAuthUserResult> createEmployee(CreateEmployeeDTO createEmployeeDTO)
            throws NotAuthenticatedException {
        return Optional.ofNullable(domainEmployeeClient.createEmployee(createEmployeeDTO));
    }

    @Cacheable(cacheNames = GET_ALL_EMPLOYEES, key = "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()")
    @Override
    public List<EmployeeDTO> getAllEmployees() throws NotAuthenticatedException {
        log.info("Cache Miss - getAllEmployees");
        return domainEmployeeClient.getAllEmployees();
    }

    @Cacheable(cacheNames = GET_EMPLOYEE_BY_ID, key = "#employeeId")
    @Override
    public Optional<EmployeeDTO> getEmployeeById(UUID employeeId) throws NotAuthenticatedException {
        log.info("Cache Miss - getEmployeeById");
        return Optional.ofNullable(domainEmployeeClient.getEmployeeById(employeeId));
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
        return Optional.ofNullable(domainEmployeeClient.updateEmployee(updateEmployeeDTO));
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

        return Optional.ofNullable(domainEmployeeClient.updateEmployeeSelfProfile(updateEmployeeDTO));
    }

    @Override
    public Optional<EmployeeDTO> addEmployeeDepartment(UUID employeeId, UUID departmentId)
            throws NotAuthenticatedException {
        return Optional.ofNullable(domainEmployeeClient.addEmployeeDepartment(employeeId, departmentId));
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
        return Optional.ofNullable(domainEmployeeClient.addEmployeeMachine(employeeId, machineId));
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
        return Optional.ofNullable(domainEmployeeClient.addEmployeeQualification(employeeId, qualificationId));
    }

    @Override
    public void removeEmployeeDepartment(UUID employeeId, UUID departmentId) throws NotAuthenticatedException {
        domainEmployeeClient.removeEmployeeDepartment(employeeId, departmentId);
    }

    @Caching(
            evict = {
                @CacheEvict(value = GET_ALL_MACHINES, key = "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()"),
                @CacheEvict(value = GET_ALL_EMPLOYEES, key = "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()")
            })
    @Override
    public void removeEmployeeMachine(UUID employeeId, UUID machineId) throws NotAuthenticatedException {
        domainEmployeeClient.removeEmployeeMachine(employeeId, machineId);
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
        domainEmployeeClient.removeEmployeeQualification(employeeId, qualificationId);
    }

    // --- DOMAIN MACHINE CLIENT (Maszyny) ---

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
    public Optional<MachineDTO> createMachine(CreateMachineDTO createMachineDTO) throws NotAuthenticatedException {
        return Optional.ofNullable(domainMachineClient.createMachine(createMachineDTO));
    }

    @Override
    public List<MachineDTO> getAllEmployeeMachinesByIds(Set<UUID> ids) throws NotAuthenticatedException {
        return domainMachineClient.getAllEmployeeMachinesByIds(ids);
    }

    @Override
    @Cacheable(cacheNames = GET_ALL_MACHINES, key = "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()")
    public List<MachineDTO> getAllMachines() throws NotAuthenticatedException {
        return domainMachineClient.getAllMachines();
    }

    @Cacheable(cacheNames = GET_ALL_MACHINE_TYPES, key = "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()")
    @Override
    public List<MachineTypeDTO> getAllMachineTypes() throws NotAuthenticatedException {
        return domainMachineClient.getAllMachineTypes();
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
    public Optional<MachineDTO> updateMachine(UpdateMachineDTO updateMachineDTO) throws NotAuthenticatedException {
        return Optional.ofNullable(domainMachineClient.updateMachine(updateMachineDTO));
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
    public void deleteMachine(UUID vehicleId) throws NotAuthenticatedException {
        domainMachineClient.deleteMachine(vehicleId);
    }

    // --- DOMAIN MESSAGE CLIENT (Wiadomości) ---

    @Override
    public void sendMessage(SendMessageCommand sendMessageCommand) throws NotAuthenticatedException {
        domainMessageClient.sendMessage(sendMessageCommand);
    }

    @Override
    public List<MessageDTO> getMessagesByRecipientEmployeeId(UUID employeeId) throws NotAuthenticatedException {
        return domainMessageClient.getMessagesByRecipientEmployeeId(employeeId);
    }

    @Override
    public Optional<MessageDTO> setMessageReadStatus(UUID messageId, boolean status) throws NotAuthenticatedException {
        return Optional.ofNullable(domainMessageClient.setMessageReadStatus(messageId, status));
    }

    // --- DOMAIN OPTION CLIENT (Ustawienia Użytkownika) ---

    @Override
    public Set<AuthUserOptionDTO> getOptionsByEmployeeId(UUID employeeId) throws NotAuthenticatedException {
        return domainOptionClient.getOptionsByEmployeeId(employeeId);
    }

    // --- DOMAIN QUALIFICATION CLIENT (Kwalifikacje) ---

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
    public Optional<QualificationDTO> createQualification(CreateQualificationDTO createQualificationDTO)
            throws NotAuthenticatedException {
        return Optional.ofNullable(deleteQualificationClient.createQualification(createQualificationDTO));
    }

    @Cacheable(cacheNames = GET_ALL_QUALIFICATIONS, key = "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()")
    @Override
    public List<QualificationDTO> getAllQualifications() throws NotAuthenticatedException {
        return deleteQualificationClient.getAllQualifications();
    }

    @Override
    public List<QualificationDTO> getAllQualificationsWithExpirationTimeByEmployeeId(UUID employeeId)
            throws NotAuthenticatedException {
        return deleteQualificationClient.getAllQualificationsWithExpirationTimeByEmployeeId(employeeId);
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
    public Optional<QualificationDTO> updateQualification(UpdateQualificationDTO updateQualificationDTO)
            throws NotAuthenticatedException {
        return Optional.ofNullable(deleteQualificationClient.updateQualification(updateQualificationDTO));
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
    public Optional<EmployeeDTO> updateQualificationExpireAt(
            UpdateQualificationExpiredAtDTO updateQualificationExpiredAtDTO) throws NotAuthenticatedException {

        return Optional.ofNullable(
                deleteQualificationClient.updateQualificationExpireAt(updateQualificationExpiredAtDTO));
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
    public void deleteQualification(UUID qualificationId) throws NotAuthenticatedException {
        deleteQualificationClient.deleteQualification(qualificationId);
    }
}
