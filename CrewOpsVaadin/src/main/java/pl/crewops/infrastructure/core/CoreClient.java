package pl.crewops.infrastructure.core;

import static pl.crewops.enums.ControllerURL.*;
import static pl.crewops.util.CacheResolver.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
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
import pl.crewops.security.ValidTokenRequest;
import pl.crewops.security.ValidTokenResponse;
import pl.crewops.util.AuthenticationResolver;
import pl.crewops.util.SpringContextBridge;

@Slf4j
@RequiredArgsConstructor
class CoreClient {

    private final RestClient coreClient;

    public PreRegisterResponse registerNewCustomer(CreateCustomerCommand command) {
        try {
            return coreClient
                    .post()
                    .uri(uriBuilder -> uriBuilder.path(REGISTER).build())
                    .body(command)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (RestClientException e) {
            log.error("Create new customer error");
            return null;
        }
    }

    public CreateCustomerResult verifyEmail(VerifyEmailRequest request) {
        try {
            return coreClient
                    .post()
                    .uri(uriBuilder -> uriBuilder.path(VERIFY_EMAIL).build())
                    .body(request)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (RestClientException e) {
            log.error("Verify email error");
            return null;
        }
    }

    // permit all for sure
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
    public AuthResponse login(AuthRequest authRequest) {
        try {
            return coreClient
                    .post()
                    .uri(uriBuilder -> uriBuilder.path(LOGIN).build())
                    .body(authRequest)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (RestClientException e) {
            log.error("Login failed" + e.getMessage());
            throw e;
        }
    }

    // authenticated BUT only own data
    @Caching(
            evict = {
                @CacheEvict(value = GET_EMPLOYEE_BY_ID, key = "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()"),
                @CacheEvict(value = GET_COMPANY_BY_ID, key = "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()")
            })
    public AuthUserDTO updateAuthUserCredentials(UpdateAuthUserDTO updateAuthUserDTO) throws NotAuthenticatedException {
        try {
            return authorizedClient()
                    .patch()
                    .uri(uriBuilder -> uriBuilder.path(UPDATE_USER_CREDENTIALS).build())
                    .body(updateAuthUserDTO)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (RestClientException e) {
            log.error("Update auth user failed");
            return null;
        }
    }

    // manager permission
    @Caching(
            evict = {
                @CacheEvict(value = GET_EMPLOYEE_BY_ID, key = "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()"),
                @CacheEvict(value = GET_COMPANY_BY_ID, key = "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()")
            })
    public AuthUserDTO updateAuthUserRoles(UpdateAuthUserDTO updateAuthUserDTO) throws NotAuthenticatedException {
        try {
            return authorizedClient()
                    .patch()
                    .uri(uriBuilder -> uriBuilder.path(UPDATE_USER_ROLES).build())
                    .body(updateAuthUserDTO)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (RestClientException e) {
            log.error("Update auth user failed");
            return null;
        }
    }

    public Set<AuthUserOptionDTO> getOptionsByEmployeeId(UUID employeeId) throws NotAuthenticatedException {
        try {
            return authorizedClient()
                    .get()
                    .uri(uriBuilder -> uriBuilder.path(EMPLOYEE_EID_OPTIONS).build(employeeId))
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (RestClientException e) {
            log.error("Get options by employee failed");
            return Set.of();
        }
    }

    // permit all or authenticated on fe side?
    public ValidTokenResponse validateToken(ValidTokenRequest validTokenRequest) {
        try {
            return coreClient
                    .post()
                    .uri(uriBuilder -> uriBuilder.path(VALIDATE).build())
                    .body(validTokenRequest)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (RestClientException e) {
            log.error("Validation failed");
            return null;
        }
    }

    // manager permission
    @Caching(
            evict = {
                @CacheEvict(value = GET_ALL_EMPLOYEES, key = "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()"),
                @CacheEvict(
                        value = GET_ALL_QUALIFICATIONS,
                        key = "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()")
            })
    public void terminateEmployeeAccount(UUID employeeId) throws NotAuthenticatedException {
        try {
            authorizedClient()
                    .delete()
                    .uri(uriBuilder -> uriBuilder
                            .path(EMPLOYEES_EID.replace("{" + EMPLOYEE_ID + "}", employeeId.toString()))
                            .build())
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException e) {
            log.error("Error deleting employee", e);
        }
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
    public EmployeeDTO addEmployeeQualification(UUID employeeId, UUID qualificationId)
            throws NotAuthenticatedException {
        try {
            return authorizedClient()
                    .patch()
                    .uri(uriBuilder -> uriBuilder
                            .path(EMPLOYEES_EID_QUALIFICATIONS_QID)
                            .build(employeeId.toString(), qualificationId.toString()))
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (RestClientException e) {
            log.error("Add employee qualification failed");
            return null;
        }
    }

    // manager permission

    @Caching(
            evict = {
                @CacheEvict(value = GET_EMPLOYEE_BY_ID, key = "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()"),
                @CacheEvict(value = GET_ALL_EMPLOYEES, key = "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()")
            })
    public CreateAuthUserResult createEmployee(CreateEmployeeDTO createEmployeeDTO) throws NotAuthenticatedException {
        try {
            return authorizedClient()
                    .post()
                    .uri(uriBuilder -> uriBuilder.path(EMPLOYEES).build())
                    .body(createEmployeeDTO)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (RestClientException e) {
            log.error("Create new employee error");
            return null;
        }
    }

    // authenticated
    public DailyEntryDTO createDailyEntry(CreateDailyEntryDTO createDailyEntryDTO) throws NotAuthenticatedException {
        try {
            return authorizedClient()
                    .post()
                    .uri(uriBuilder -> uriBuilder.path(DAILY_ENTRIES).build())
                    .body(createDailyEntryDTO)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (RestClientException e) {
            log.error("Create new daily entry error");
            return null;
        }
    }

    // authenticated but regular user can fetch only his own daily entry
    public DailyEntryDTO findDailyEntryByEmployeeIdAndDate(UUID employeeId, LocalDate localDate)
            throws NotAuthenticatedException {
        try {
            return authorizedClient()
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path(DAILY_ENTRIES)
                            .queryParam("employeeId", employeeId)
                            .queryParam("entryDate", localDate)
                            .build())
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (RestClientException e) {
            log.error("Get daily entry by employee id and entry date failed");
            return null;
        }
    }

    // manager permission
    // authenticated BUT logged user can update only his own data !!!!!!!!!!!!!!!!!!!!!!!!!!
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
    public EmployeeDTO updateEmployee(UpdateEmployeeDTO updateEmployeeDTO) throws NotAuthenticatedException {
        try {
            return authorizedClient()
                    .patch()
                    .uri(uriBuilder -> uriBuilder.path(EMPLOYEES_EID).build(updateEmployeeDTO.employeeId()))
                    .body(updateEmployeeDTO)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (RestClientException e) {
            log.error("Update employee error");
            return null;
        }
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
    public EmployeeDTO updateEmployeeSelfProfile(UpdateEmployeeDTO updateEmployeeDTO) throws NotAuthenticatedException {
        try {
            return authorizedClient()
                    .put()
                    .uri(uriBuilder -> uriBuilder.path(EMPLOYEES_EID).build(updateEmployeeDTO.employeeId()))
                    .body(updateEmployeeDTO)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (RestClientException e) {
            log.error("Update employee error");
            return null;
        }
    }

    // authenticated
    @Cacheable(cacheNames = GET_EMPLOYEE_BY_ID, key = "#employeeId")
    public EmployeeDTO getEmployeeById(UUID employeeId) throws NotAuthenticatedException {
        log.info("Get employee by id cache missed");
        try {
            return authorizedClient()
                    .get()
                    .uri(uriBuilder -> uriBuilder.path(EMPLOYEES_EID).build(employeeId))
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (RestClientException e) {
            log.error("Error getting employee by id");
            return null;
        }
    }

    // authenticated
    public EmployeeDTO getEmployeeByIdNoCache(UUID employeeId) throws NotAuthenticatedException {
        log.info("Get employee by id cache missed");
        try {
            return authorizedClient()
                    .get()
                    .uri(uriBuilder -> uriBuilder.path(EMPLOYEES_EID).build(employeeId))
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (RestClientException e) {
            log.error("Error getting employee by id");
            return null;
        }
    }

    // authenticated
    @Cacheable(cacheNames = GET_ALL_EMPLOYEES, key = "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()")
    public List<EmployeeDTO> getAllEmployees() throws NotAuthenticatedException {
        log.info("Get all employees cache missed");
        try {
            return authorizedClient()
                    .get()
                    .uri(uriBuilder -> uriBuilder.path(EMPLOYEES).build())
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (RestClientException e) {
            log.error("Error getting employees");
            return List.of();
        }
    }

    // manager permission
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
    public QualificationDTO createQualification(CreateQualificationDTO createQualificationDTO)
            throws NotAuthenticatedException {
        try {
            return authorizedClient()
                    .post()
                    .uri(uriBuilder -> uriBuilder.path(QUALIFICATIONS).build())
                    .body(createQualificationDTO)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (RestClientException e) {
            log.error("Create new qualification error");
            return null;
        }
    }

    // manager permission
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
    public QualificationDTO updateQualification(UpdateQualificationDTO updateQualificationDTO)
            throws NotAuthenticatedException {
        try {
            return authorizedClient()
                    .patch()
                    .uri(uriBuilder ->
                            uriBuilder.path(QUALIFICATIONS_QID).build(updateQualificationDTO.qualificationId()))
                    .body(updateQualificationDTO)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (RestClientException e) {
            log.error("Update qualification error");
            return null;
        }
    }

    // manager permission
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
    public EmployeeDTO updateQualificationExpireAt(UpdateQualificationExpiredAtDTO updateQualificationExpiredAtDTO)
            throws NotAuthenticatedException {
        try {
            return authorizedClient()
                    .patch()
                    .uri(uriBuilder -> uriBuilder
                            .path(EMPLOYEES_EID_QUALIFICATIONS_QID_EXPIRED)
                            .build(
                                    updateQualificationExpiredAtDTO.employeeId(),
                                    updateQualificationExpiredAtDTO.qualificationId()))
                    .body(updateQualificationExpiredAtDTO)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (RestClientException e) {
            log.error("Update qualification expired at error");
            return null;
        }
    }

    public void sendMessage(SendMessageCommand sendMessageCommand) throws NotAuthenticatedException {
        try {
            authorizedClient()
                    .post()
                    .uri(uriBuilder -> uriBuilder.path(MESSAGES).build())
                    .body(sendMessageCommand)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException e) {
            log.error("Update qualification expired at error");
        }
    }

    public MessageDTO setMessageReadStatus(UUID messageId, boolean status) throws NotAuthenticatedException {
        try {
            return authorizedClient()
                    .patch()
                    .uri(uriBuilder -> uriBuilder.path(MESSAGES_MID).build(messageId))
                    .body(status)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (RestClientException e) {
            log.error("Update message read status error");
            return null;
        }
    }

    // manager permission
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
    public void removeEmployeeQualification(UUID employeeId, UUID qualificationId) throws NotAuthenticatedException {
        try {
            authorizedClient()
                    .delete()
                    .uri(uriBuilder ->
                            uriBuilder.path(EMPLOYEES_EID_QUALIFICATIONS_QID).build(employeeId, qualificationId))
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException e) {
            log.error("Remove employee qualification error");
        }
    }

    // manager permission
    @Caching(
            evict = {
                @CacheEvict(value = GET_ALL_MACHINES, key = "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()"),
                @CacheEvict(value = GET_ALL_EMPLOYEES, key = "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()")
            })
    public void removeEmployeeMachine(UUID employeeId, UUID machineId) throws NotAuthenticatedException {
        try {
            authorizedClient()
                    .delete()
                    .uri(uriBuilder ->
                            uriBuilder.path(EMPLOYEES_EID_MACHINES_VID).build(employeeId, machineId))
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException e) {
            log.error("Remove employee machine error");
        }
    }

    // manager permission
    // todo: consider cache by employee id
    public List<QualificationDTO> getAllQualificationsWithExpirationTimeByEmployeeId(UUID employeeId)
            throws NotAuthenticatedException {
        try {
            return authorizedClient()
                    .get()
                    .uri(uriBuilder ->
                            uriBuilder.path(QUALIFICATIONS_EID_EXPIRED).build(employeeId))
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (RestClientException e) {
            log.error("Get all qualifications with expiration time by employee id error");
            return List.of();
        }
    }

    // authenticated
    @Cacheable(cacheNames = GET_ALL_QUALIFICATIONS, key = "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()")
    public List<QualificationDTO> getAllQualifications() throws NotAuthenticatedException {
        log.info("Get all qualifications cache missed");
        try {
            return authorizedClient()
                    .get()
                    .uri(uriBuilder -> uriBuilder.path(QUALIFICATIONS).build())
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<QualificationDTO>>() {});
        } catch (RestClientException e) {
            log.error("Error getting qualifications");
            return List.of();
        }
    }

    // manager permission
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
    public void deleteQualification(UUID qualificationId) throws NotAuthenticatedException {
        try {
            authorizedClient()
                    .delete()
                    .uri(uriBuilder -> uriBuilder.path(QUALIFICATIONS_QID).build(qualificationId))
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException e) {
            log.error("Error deleting qualification", e);
        }
    }

    // manager permission
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
    public MachineDTO createMachine(CreateMachineDTO createMachineDTO) throws NotAuthenticatedException {
        try {
            return authorizedClient()
                    .post()
                    .uri(uriBuilder -> uriBuilder.path(MACHINES).build())
                    .body(createMachineDTO)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (RestClientException e) {
            log.error("Create new employee error", e);
            return null;
        }
    }
    // manager permission or mechanic authority?

    // shift leader or mechanic
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
    public MachineDTO updateMachine(UpdateMachineDTO updateMachineDTO) throws NotAuthenticatedException {
        try {
            return authorizedClient()
                    .patch()
                    .uri(uriBuilder -> uriBuilder.path(MACHINES_VID).build(updateMachineDTO.machineId()))
                    .body(updateMachineDTO)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (RestClientException e) {
            log.error("Update employee error", e);
            return null;
        }
    }

    // authenticated
    @Cacheable(cacheNames = GET_ALL_MACHINES, key = "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()")
    public List<MachineDTO> getAllMachines() throws NotAuthenticatedException {
        log.info("Get all machines cache missed");
        try {
            return authorizedClient()
                    .get()
                    .uri(uriBuilder -> uriBuilder.path(MACHINES).build())
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<MachineDTO>>() {});
        } catch (RestClientException e) {
            log.error("Error getting machines");
            return List.of();
        }
    }

    // manager permission
    //    @Cacheable(cacheNames = GET_ALL_MACHINES_BY_IDS, key =
    // "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()")
    //    todo: consider cache by ids
    public List<MachineDTO> getAllEmployeeMachinesByIds(Set<UUID> ids) throws NotAuthenticatedException {
        try {
            return authorizedClient()
                    .post()
                    .uri(uriBuilder -> uriBuilder.path(MACHINES_VIDS).build())
                    .body(ids)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (RestClientException e) {
            log.error("Get all employee machine ids error");
            return List.of();
        }
    }

    // authenticated
    @Cacheable(cacheNames = GET_ALL_MACHINE_TYPES, key = "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()")
    public List<MachineTypeDTO> getAllMachineTypes() throws NotAuthenticatedException {
        log.info("Get all machine types cache missed");
        try {
            return authorizedClient()
                    .get()
                    .uri(uriBuilder -> uriBuilder.path(MACHINE_TYPES).build())
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<MachineTypeDTO>>() {});
        } catch (RestClientException e) {
            log.error("Error getting machine types");
            return List.of();
        }
    }

    // manager permission
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
    public EmployeeDTO addEmployeeMachine(UUID employeeId, UUID machineId) throws NotAuthenticatedException {
        try {
            return authorizedClient()
                    .patch()
                    .uri(uriBuilder ->
                            uriBuilder.path(EMPLOYEES_EID_MACHINES_VID).build(employeeId, machineId))
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (RestClientException e) {
            log.error("Add employee machine error", e);
            return null;
        }
    }

    // manager permission
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
    public void deleteMachine(UUID machineId) throws NotAuthenticatedException {
        try {
            authorizedClient()
                    .delete()
                    .uri(uriBuilder -> uriBuilder
                            .path(MACHINES_VID.replace("{" + MACHINE_ID + "}", machineId.toString()))
                            .build())
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException e) {
            log.error("Error deleting machine", e);
        }
    }

    // authenticated
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
    public BreakdownDTO createBreakdown(CreateBreakdownDTO createBreakdownDTO) throws NotAuthenticatedException {
        try {
            return authorizedClient()
                    .post()
                    .uri(uriBuilder -> uriBuilder.path(BREAKDOWNS).build())
                    .body(createBreakdownDTO)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (RestClientException e) {
            log.error("Create new breakdown error");
            return null;
        }
    }

    // shift leader or mechanic
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
    public BreakdownDTO updateBreakdown(UpdateBreakdownDTO updateBreakdownDTO) throws NotAuthenticatedException {
        try {
            return authorizedClient()
                    .patch()
                    .uri(uriBuilder -> uriBuilder.path(BREAKDOWNS_BID).build((updateBreakdownDTO.breakdownId())))
                    .body(updateBreakdownDTO)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (RestClientException e) {
            log.error("Update breakdown error", e);
            return null;
        }
    }

    // authenticated
    @Cacheable(cacheNames = GET_ALL_BREAKDOWNS, key = "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()")
    public List<BreakdownDTO> getAllBreakdowns() throws NotAuthenticatedException {
        log.info("Get all breakdowns cache missed");
        try {
            return authorizedClient()
                    .get()
                    .uri(uriBuilder -> uriBuilder.path(BREAKDOWNS).build())
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<BreakdownDTO>>() {});
        } catch (RestClientException e) {
            log.error("Error getting breakdowns");
            return List.of();
        }
    }

    // authenticated
    @Cacheable(cacheNames = GET_ALL_DEPARTMENTS, key = "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()")
    public List<DepartmentDTO> getAllDepartments() throws NotAuthenticatedException {
        log.info("Get all departments cache missed");
        try {
            return authorizedClient()
                    .get()
                    .uri(uriBuilder -> uriBuilder.path(DEPARTMENTS).build())
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (RestClientException e) {
            log.error("Error getting departments");
            return List.of();
        }
    }

    public Set<DepartmentDTO> getAllDepartmentsByIds(Set<UUID> departmentIds) throws NotAuthenticatedException {
        log.info("Get all departments by ids cache missed");
        try {
            return authorizedClient()
                    .post()
                    .uri(uriBuilder -> uriBuilder.path(DEPARTMENTS_DIDS).build())
                    .body(departmentIds)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (RestClientException e) {
            log.error("Error getting departments by ids");
            return Set.of();
        }
    }

    public EmployeeDTO addEmployeeDepartment(UUID employeeId, UUID departmentId) throws NotAuthenticatedException {
        log.info("Assignment department to employee");
        try {
            return authorizedClient()
                    .patch()
                    .uri(uriBuilder -> uriBuilder
                            .path(EMPLOYEES_EID_DEPARTMENTS_DID)
                            .build(employeeId.toString(), departmentId.toString()))
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (RestClientException e) {
            log.error("Error during add department to employee");
            return null;
        }
    }

    public void removeEmployeeDepartment(UUID employeeId, UUID departmentId) throws NotAuthenticatedException {
        try {
            authorizedClient()
                    .delete()
                    .uri(uriBuilder -> uriBuilder
                            .path(EMPLOYEES_EID_DEPARTMENTS_DID)
                            .build(employeeId.toString(), departmentId.toString()))
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException e) {
            log.error("Error during remove department from employee");
        }
    }

    // authenticated
    // TODO: consider remove caching of this value or implement different logic
    @Cacheable(cacheNames = GET_COMPANY_BY_ID, key = "#companyId")
    public CompanyDTO getCompanyById(UUID companyId) throws NotAuthenticatedException {
        log.info("Get company by id cache missed");
        try {
            return authorizedClient()
                    .get()
                    .uri(uriBuilder -> uriBuilder.path(COMPANIES_CID).build(companyId))
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (RestClientException e) {
            log.error("Error getting company by id");
            return null;
        }
    }

    public List<MessageDTO> getMessagesByRecipientEmployeeId(UUID employeeId) throws NotAuthenticatedException {
        try {
            return authorizedClient()
                    .get()
                    .uri(uriBuilder -> uriBuilder.path(MESSAGES_EID).build(employeeId))
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (RestClientException e) {
            log.error("Error getting messages by recipient employee");
            return List.of();
        }
    }

    private RestClient authorizedClient() throws NotAuthenticatedException {
        AuthenticationResolver authenticationResolver = SpringContextBridge.getBean(AuthenticationResolver.class);
        if (authenticationResolver.getPrincipal() != null) {
            return coreClient
                    .mutate()
                    .defaultHeader(
                            "Authorization",
                            "Bearer " + authenticationResolver.getPrincipal().getToken())
                    .build();
        } else {
            throw new NotAuthenticatedException();
        }
    }
}
