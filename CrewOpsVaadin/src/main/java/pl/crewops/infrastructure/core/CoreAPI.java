package pl.crewops.infrastructure.core;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.stereotype.Repository;
import org.springframework.validation.annotation.Validated;
import pl.crewops.dto.auth.*;
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
import pl.crewops.dto.message.MessageDTO;
import pl.crewops.dto.message.SendMessageCommand;
import pl.crewops.dto.qualification.CreateQualificationDTO;
import pl.crewops.dto.qualification.QualificationDTO;
import pl.crewops.dto.qualification.UpdateQualificationDTO;
import pl.crewops.dto.qualification.UpdateQualificationExpiredAtDTO;
import pl.crewops.exceptions.NotAuthenticatedException;
import pl.crewops.registration.CreateCustomerCommand;
import pl.crewops.registration.CreateCustomerResult;

@Repository
@Validated
public interface CoreAPI {

    Optional<AuthResponse> login(@Valid @NotNull AuthRequest request);

    Optional<AuthUserDTO> updateAuthUserRoles(@Valid @NotNull UpdateAuthUserDTO updateAuthUserDTO)
            throws NotAuthenticatedException;

    Optional<ValidTokenResponse> validateToken(@Valid @NotNull ValidTokenRequest validTokenRequest);

    Optional<CreateAuthUserResult> createEmployee(@Valid @NotNull CreateEmployeeDTO createEmployeeDTO)
            throws NotAuthenticatedException;

    Optional<EmployeeDTO> updateEmployee(UpdateEmployeeDTO updateEmployeeDTO) throws NotAuthenticatedException;

    Optional<EmployeeDTO> addEmployeeQualification(@NotNull UUID employeeId, @NotNull UUID qualificationId)
            throws NotAuthenticatedException;

    Optional<QualificationDTO> createQualification(@Valid @NotNull CreateQualificationDTO createQualificationDTO)
            throws NotAuthenticatedException;

    Optional<QualificationDTO> updateQualification(@Valid @NotNull UpdateQualificationDTO updateQualificationDTO)
            throws NotAuthenticatedException;

    Optional<EmployeeDTO> updateQualificationExpireAt(
            @Valid @NotNull UpdateQualificationExpiredAtDTO updateQualificationExpiredAtDTO)
            throws NotAuthenticatedException;

    List<QualificationDTO> getAllQualificationsWithExpirationTimeByEmployeeId(@NotNull UUID employeeId)
            throws NotAuthenticatedException;

    Optional<MachineDTO> createMachine(@Valid @NotNull CreateMachineDTO createMachineDTO)
            throws NotAuthenticatedException;

    Optional<CreateCustomerResult> registerNewCustomer(@Valid @NotNull CreateCustomerCommand command)
            throws NotAuthenticatedException;

    Optional<MachineDTO> updateMachine(@Valid @NotNull UpdateMachineDTO updateMachineDTO)
            throws NotAuthenticatedException;

    Optional<BreakdownDTO> createBreakdown(@Valid @NotNull CreateBreakdownDTO createBreakdownDTO)
            throws NotAuthenticatedException;

    Optional<BreakdownDTO> updateBreakdown(@Valid @NotNull UpdateBreakdownDTO updateBreakdownDTO)
            throws NotAuthenticatedException;

    List<EmployeeDTO> getAllEmployees() throws NotAuthenticatedException;

    Optional<EmployeeDTO> getEmployeeById(UUID employeeId) throws NotAuthenticatedException;

    Optional<EmployeeDTO> getEmployeeByIdNoCache(UUID employeeId) throws NotAuthenticatedException;

    List<QualificationDTO> getAllQualifications() throws NotAuthenticatedException;

    List<MachineDTO> getAllMachines() throws NotAuthenticatedException;

    List<MachineDTO> getAllEmployeeMachinesByIds(@NotNull Set<UUID> ids) throws NotAuthenticatedException;

    List<MachineTypeDTO> getAllMachineTypes() throws NotAuthenticatedException;

    Optional<EmployeeDTO> addEmployeeMachine(@NotNull UUID employeeId, @NotNull UUID machineId)
            throws NotAuthenticatedException;

    List<BreakdownDTO> getAllBreakdowns() throws NotAuthenticatedException;

    Optional<CompanyDTO> getCompanyById(@NotNull UUID companyId) throws NotAuthenticatedException;

    List<MessageDTO> getMessagesByRecipientEmployeeId(@NotNull UUID employeeId) throws NotAuthenticatedException;

    Optional<MessageDTO> setMessageReadStatus(@NotNull UUID messageId, boolean status) throws NotAuthenticatedException;

    void sendMessage(@NotNull SendMessageCommand sendMessageCommand) throws NotAuthenticatedException;

    void terminateEmployeeAccount(@NotNull UUID employeeId) throws NotAuthenticatedException;

    void removeEmployeeQualification(@NotNull UUID employeeId, @NotNull UUID qualificationId)
            throws NotAuthenticatedException;

    void removeEmployeeMachine(@NotNull UUID employeId, @NotNull UUID machineId) throws NotAuthenticatedException;

    void deleteQualification(@NotNull UUID qualificationId) throws NotAuthenticatedException;

    void deleteMachine(@NotNull UUID machineId) throws NotAuthenticatedException;
}
