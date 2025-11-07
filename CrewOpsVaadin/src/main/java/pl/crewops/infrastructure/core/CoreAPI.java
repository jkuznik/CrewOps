package pl.crewops.infrastructure.core;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.stereotype.Repository;
import org.springframework.validation.annotation.Validated;
import pl.crewops.exceptions.NotAuthenticatedException;
import pl.crewops.model.dto.auth.*;
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
import pl.crewops.model.dto.note.CreateNoteDTO;
import pl.crewops.model.dto.note.FetchNotesRequest;
import pl.crewops.model.dto.note.NoteDTO;
import pl.crewops.model.dto.option.AuthUserOptionDTO;
import pl.crewops.model.dto.qualification.CreateQualificationDTO;
import pl.crewops.model.dto.qualification.QualificationDTO;
import pl.crewops.model.dto.qualification.UpdateQualificationDTO;
import pl.crewops.model.dto.qualification.UpdateQualificationExpiredAtDTO;
import pl.crewops.model.dto.registration.CreateCustomerCommand;
import pl.crewops.model.dto.registration.CreateCustomerResult;
import pl.crewops.model.dto.registration.PreRegisterResponse;
import pl.crewops.model.dto.registration.VerifyEmailRequest;

@Repository
@Validated
public interface CoreAPI {

    // --- AUTHENTICATION & REGISTRATION ---

    Optional<AuthResponse> login(@Valid @NotNull AuthRequest request);

    Optional<CreateCustomerResult> verifyEmail(@Valid @NotNull VerifyEmailRequest request);

    Optional<AuthUserDTO> updateAuthUserCredentials(@Valid @NotNull UpdateAuthUserDTO updateAuthUserDTO)
            throws NotAuthenticatedException;

    Optional<AuthUserDTO> updateAuthUserRoles(@Valid @NotNull UpdateAuthUserDTO updateAuthUserDTO)
            throws NotAuthenticatedException;

    Optional<PreRegisterResponse> registerNewCustomer(@Valid @NotNull CreateCustomerCommand command);

    void terminateEmployeeAccount(@NotNull UUID employeeId) throws NotAuthenticatedException;

    // --- EMPLOYEE ---

    Set<AuthUserOptionDTO> getOptionsByEmployeeId(@NotNull UUID employeeId) throws NotAuthenticatedException;

    Optional<CreateAuthUserResult> createEmployee(@Valid @NotNull CreateEmployeeDTO createEmployeeDTO)
            throws NotAuthenticatedException;

    Optional<EmployeeDTO> updateEmployee(@Valid @NotNull UpdateEmployeeDTO updateEmployeeDTO)
            throws NotAuthenticatedException;

    Optional<EmployeeDTO> updateEmployeeSelfProfile(@Valid @NotNull UpdateEmployeeDTO updateEmployeeDTO)
            throws NotAuthenticatedException;

    Optional<EmployeeDTO> addEmployeeQualification(@NotNull UUID employeeId, @NotNull UUID qualificationId)
            throws NotAuthenticatedException;

    Optional<EmployeeDTO> addEmployeeMachine(@NotNull UUID employeeId, @NotNull UUID machineId)
            throws NotAuthenticatedException;

    Optional<EmployeeDTO> addEmployeeDepartment(@NotNull UUID employeeId, @NotNull UUID departmentId)
            throws NotAuthenticatedException;

    List<EmployeeDTO> getAllEmployees() throws NotAuthenticatedException;

    Optional<EmployeeDTO> getEmployeeById(@NotNull UUID employeeId) throws NotAuthenticatedException;

    void removeEmployeeDepartment(@NotNull UUID employeeId, @NotNull UUID departmentId)
            throws NotAuthenticatedException;

    void removeEmployeeQualification(@NotNull UUID employeeId, @NotNull UUID qualificationId)
            throws NotAuthenticatedException;

    void removeEmployeeMachine(@NotNull UUID employeId, @NotNull UUID machineId) throws NotAuthenticatedException;

    // --- QUALIFICATION ---

    Optional<QualificationDTO> createQualification(@Valid @NotNull CreateQualificationDTO createQualificationDTO)
            throws NotAuthenticatedException;

    Optional<QualificationDTO> updateQualification(@Valid @NotNull UpdateQualificationDTO updateQualificationDTO)
            throws NotAuthenticatedException;

    Optional<EmployeeDTO> updateQualificationExpireAt(
            @Valid @NotNull UpdateQualificationExpiredAtDTO updateQualificationExpiredAtDTO)
            throws NotAuthenticatedException;

    List<QualificationDTO> getAllQualificationsWithExpirationTimeByEmployeeId(@NotNull UUID employeeId)
            throws NotAuthenticatedException;

    List<QualificationDTO> getAllQualifications() throws NotAuthenticatedException;

    void deleteQualification(@NotNull UUID qualificationId) throws NotAuthenticatedException;

    // --- MACHINE ---

    Optional<MachineDTO> createMachine(@Valid @NotNull CreateMachineDTO createMachineDTO)
            throws NotAuthenticatedException;

    Optional<MachineDTO> updateMachine(@Valid @NotNull UpdateMachineDTO updateMachineDTO)
            throws NotAuthenticatedException;

    List<MachineDTO> getAllMachines() throws NotAuthenticatedException;

    List<MachineDTO> getAllEmployeeMachinesByIds(@NotNull Set<UUID> ids) throws NotAuthenticatedException;

    List<MachineTypeDTO> getAllMachineTypes() throws NotAuthenticatedException;

    void deleteMachine(@NotNull UUID machineId) throws NotAuthenticatedException;

    // --- BREAKDOWN ---

    Optional<BreakdownDTO> createBreakdown(@Valid @NotNull CreateBreakdownDTO createBreakdownDTO)
            throws NotAuthenticatedException;

    Optional<BreakdownDTO> updateBreakdown(@Valid @NotNull UpdateBreakdownDTO updateBreakdownDTO)
            throws NotAuthenticatedException;

    List<BreakdownDTO> getAllBreakdowns() throws NotAuthenticatedException;

    // --- DEPARTMENT ---

    List<DepartmentDTO> getAllDepartments() throws NotAuthenticatedException;

    // --- COMPANY ---

    Optional<CompanyDTO> getCompanyById(@NotNull UUID companyId) throws NotAuthenticatedException;

    // --- MESSAGE ---

    List<MessageDTO> getMessagesByRecipientEmployeeId(@NotNull UUID employeeId) throws NotAuthenticatedException;

    Optional<MessageDTO> setMessageReadStatus(@NotNull UUID messageId, boolean status) throws NotAuthenticatedException;

    void sendMessage(@NotNull SendMessageCommand sendMessageCommand) throws NotAuthenticatedException;

    // --- DAILY ENTRY ---

    Optional<DailyEntryDTO> createDailyEntry(@Valid @NotNull CreateDailyEntryDTO createDailyEntryDTO)
            throws NotAuthenticatedException;

    Optional<DailyEntryDTO> findDailyEntryByEmployeeIdAndDate(@NotNull UUID employeeId, @NotNull LocalDate localDate)
            throws NotAuthenticatedException;

    Optional<DailyEntryDTO> updateDailyEntrySelfPermission(
            @Valid @NotNull UpdateDailyEntryCommand updateDailyEntryCommand) throws NotAuthenticatedException;

    Optional<DailyEntryDTO> approveDailyEntry(@Valid @NotNull UpdateDailyEntryCommand updateDailyEntryCommand)
            throws NotAuthenticatedException;

    // --- DAILY NOTE ---

    Optional<NoteDTO> createNote(@Valid @NotNull CreateNoteDTO createNoteDTO) throws NotAuthenticatedException;

    List<NoteDTO> getAllPublicAndPrincipalPrivateNotesByDate(@NotNull FetchNotesRequest fetchNotesRequest)
            throws NotAuthenticatedException;

    // ---- JOB POSITION ---

    JobPositionDTO createJobPosition(@NotNull @Valid CreateJobPositionDTO createJobPositionDTO)
            throws NotAuthenticatedException;

    List<JobPositionDTO> getAllJobPositions() throws NotAuthenticatedException;

    JobPositionDTO updateJobPosition(@NotNull @Valid UpdateJobPositionDTO updateJobPositionDTO)
            throws NotAuthenticatedException;

    void deleteJobPositionById(@NotNull UUID id) throws NotAuthenticatedException;
}
