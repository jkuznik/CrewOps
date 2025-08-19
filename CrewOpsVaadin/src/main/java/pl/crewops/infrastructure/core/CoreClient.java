package pl.crewops.infrastructure.core;

import static pl.crewops.enums.ControllerURL.*;
import static pl.crewops.util.CacheResolver.*;

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
import pl.crewops.dto.qualification.CreateQualificationDTO;
import pl.crewops.dto.qualification.QualificationDTO;
import pl.crewops.dto.qualification.UpdateQualificationDTO;
import pl.crewops.dto.qualification.UpdateQualificationExpiredAtDTO;
import pl.crewops.exceptions.NotAuthenticatedException;
import pl.crewops.registration.CreateCustomerCommand;
import pl.crewops.registration.CreateCustomerResult;
import pl.crewops.util.AuthenticationResolver;
import pl.crewops.util.SpringContextBridge;

@Slf4j
@RequiredArgsConstructor
class CoreClient {

    private final RestClient coreClient;

    public CreateCustomerResult registerNewCustomer(CreateCustomerCommand command) throws NotAuthenticatedException {
        try {
            return authorizedClient()
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
    // permit all for sure

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
                    .uri(uriBuilder -> uriBuilder.path(UPDATE_ROLES).build())
                    .body(updateAuthUserDTO)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (RestClientException e) {
            log.error("Update auth user failed");
            return null;
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
                @CacheEvict(value = GET_EMPLOYEE_BY_ID, key = "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()"),
                @CacheEvict(value = GET_COMPANY_BY_ID, key = "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()")
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
    public EmployeeDTO createEmployee(CreateEmployeeDTO createEmployeeDTO) throws NotAuthenticatedException {
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

    // TODO: consider about implement security on fe side
    // manager permission
    // authenticated
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

    // authenticated
    @Cacheable(cacheNames = GET_EMPLOYEE_BY_ID, key = "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()")
    public EmployeeDTO getEmployeeById(UUID employeeId) throws NotAuthenticatedException {
        log.warn("Get employee by id cache missing");
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
        log.warn("Get all employees cache missing");
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
        log.warn("Get all qualifications cache missing");
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
        log.warn("Get all machines cache missing");
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
    // TODO: consider remove caching of this value or implement different logic
    @Cacheable(cacheNames = GET_COMPANY_BY_ID, key = "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()")
    public CompanyDTO getCompanyById(UUID companyId) throws NotAuthenticatedException {
        log.warn("Get company by id cache missing");
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
