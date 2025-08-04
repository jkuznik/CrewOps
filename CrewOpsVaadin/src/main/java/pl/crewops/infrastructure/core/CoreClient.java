package pl.crewops.infrastructure.core;

import static pl.crewops.enums.ControllerURL.*;
import static pl.crewops.util.CacheResolver.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
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
import pl.crewops.registration.CreateCustomerCommand;
import pl.crewops.registration.CreateCustomerResult;

@Slf4j
@RequiredArgsConstructor
class CoreClient {

    private final RestClient coreClient;
    private RestClient authorizedClient;

    public CreateCustomerResult registerNewCustomer(CreateCustomerCommand command) {
        try {
            return authorizedClient
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

    // permit all for sure
    //    @Caching(
    //            evict = {
    //                @CacheEvict(value = GET_EMPLOYEE_BY_ID, allEntries = true),
    //                @CacheEvict(value = GET_COMPANY_BY_ID, allEntries = true)
    //            })
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

    // permit all or authenticated on fe side?
    public Optional<ValidTokenResponse> validateToken(ValidTokenRequest validTokenRequest) {
        try {
            ValidTokenResponse body = coreClient
                    .post()
                    .uri(uriBuilder -> uriBuilder.path(VALIDATE).build())
                    .body(validTokenRequest)
                    .retrieve()
                    .body(new ParameterizedTypeReference<ValidTokenResponse>() {});
            return Optional.ofNullable(body);
        } catch (RestClientException e) {
            log.error("Validation failed");
            return Optional.empty();
        }
    }

    // manager permission
    //    @CacheEvict(value = GET_ALL_EMPLOYEES, key = "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()")
    public void terminateEmployeeAccount(UUID employeeId) {
        try {
            authorizedClient
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

    // manager permission
    //    @CacheEvict(value = GET_ALL_EMPLOYEES, key = "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()")
    public EmployeeDTO createEmployee(CreateEmployeeDTO createEmployeeDTO) {
        try {
            return authorizedClient
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
    //    @Caching(
    //            evict = {
    //                    @CacheEvict(value = GET_ALL_QUALIFICATIONS, key =
    // "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()"),
    //                    @CacheEvict(value = GET_ALL_EMPLOYEES, key =
    // "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()")
    //            })
    public EmployeeDTO updateEmployee(UpdateEmployeeDTO updateEmployeeDTO) {
        try {
            return authorizedClient
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
    //        @Cacheable(cacheNames = GET_EMPLOYEE_BY_ID, key =
    // "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()")
    public EmployeeDTO getEmployeeById(UUID employeeId) {
        log.warn("Get employee by id cache missing");
        try {
            return authorizedClient
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
    //        @Cacheable(cacheNames = GET_ALL_EMPLOYEES, key = "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()")
    public List<EmployeeDTO> getAllEmployees() {
        log.warn("Get all employees cache missing");
        try {
            return authorizedClient
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
    //    @CacheEvict(value = GET_ALL_QUALIFICATIONS, key = "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()")
    public QualificationDTO createQualification(CreateQualificationDTO createQualificationDTO) {
        try {
            return authorizedClient
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
    //    @Caching(
    //            evict = {
    //                    @CacheEvict(value = GET_ALL_QUALIFICATIONS, key =
    // "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()"),
    //                    @CacheEvict(value = GET_ALL_EMPLOYEES, key =
    // "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()")
    //            })
    public QualificationDTO updateQualification(UpdateQualificationDTO updateQualificationDTO) {
        try {
            return authorizedClient
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

    // authenticated
    //        @Cacheable(cacheNames = GET_ALL_QUALIFICATIONS, key =
    // "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()")
    public List<QualificationDTO> getAllQualifications() {
        log.warn("Get all qualifications cache missing");
        try {
            return authorizedClient
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
    //    @Caching(
    //            evict = {
    //                    @CacheEvict(value = GET_ALL_QUALIFICATIONS, key =
    // "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()"),
    //                    @CacheEvict(value = GET_ALL_EMPLOYEES, key =
    // "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()")
    //            })
    public void deleteQualification(UUID qualificationId) {
        try {
            authorizedClient
                    .delete()
                    .uri(uriBuilder -> uriBuilder
                            .path(QUALIFICATIONS_QID.replace("{" + QUALIFICATION_ID + "}", qualificationId.toString()))
                            .build())
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException e) {
            log.error("Error deleting qualification", e);
        }
    }

    //    @Caching(
    //            evict = {
    //                    @CacheEvict(value = GET_ALL_BREAKDOWNS, key =
    // "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()"),
    //                @CacheEvict(value = GET_ALL_MACHINES, key =
    // "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()"),
    //                @CacheEvict(
    //                        value = GET_ALL_MACHINE_TYPES,
    //                        key = "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()")
    //            })
    public MachineDTO createMachine(CreateMachineDTO createMachineDTO) {
        try {
            return authorizedClient
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
    //    @Caching(
    //            evict = {
    //                    @CacheEvict(value = GET_ALL_BREAKDOWNS, key =
    // "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()"),
    //                @CacheEvict(value = GET_ALL_MACHINES, key =
    // "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()"),
    //                @CacheEvict(
    //                        value = GET_ALL_MACHINE_TYPES,
    //                        key = "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()"),
    //
    //            })
    public MachineDTO updateMachine(UpdateMachineDTO updateMachineDTO) {
        try {
            return authorizedClient
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
    //        @Cacheable(cacheNames = GET_ALL_MACHINES, key = "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()")
    public List<MachineDTO> getAllMachines() {
        log.warn("Get all machines cache missing");
        try {
            return authorizedClient
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

    // authenticated
    //        @Cacheable(cacheNames = GET_ALL_MACHINE_TYPES, key =
    // "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()")
    public List<MachineTypeDTO> getAllMachineTypes() {
        try {
            return authorizedClient
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
    //    @Caching(
    //            evict = {
    //                    @CacheEvict(value = GET_ALL_MACHINES, key =
    // "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()"),
    //                    @CacheEvict(value = GET_ALL_MACHINE_TYPES, key =
    // "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()")
    //            }
    //    )
    public void deleteMachine(UUID machineId) {
        try {
            authorizedClient
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
    //    @Caching(
    //            evict = {
    //                    @CacheEvict(value = GET_ALL_BREAKDOWNS, key =
    // "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()"),
    //                    @CacheEvict(value = GET_ALL_MACHINES, key =
    // "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()"),
    //                    @CacheEvict(value = GET_ALL_MACHINE_TYPES, key =
    // "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()")
    //            }
    //    )
    public BreakdownDTO createBreakdown(CreateBreakdownDTO createBreakdownDTO) {
        try {
            return authorizedClient
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
    //    @Caching(
    //            evict = {
    //                    @CacheEvict(value = GET_ALL_BREAKDOWNS, key =
    // "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()"),
    //                    @CacheEvict(value = GET_ALL_MACHINES, key =
    // "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()"),
    //                    @CacheEvict(value = GET_ALL_MACHINE_TYPES, key =
    // "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()")
    //            })

    public BreakdownDTO updateBreakdown(UpdateBreakdownDTO updateBreakdownDTO) {
        try {
            return authorizedClient
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
    public List<BreakdownDTO> getAllBreakdowns() {
        try {
            return authorizedClient
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
    //        @Cacheable(cacheNames = GET_COMPANY_BY_ID, key = "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()")
    public CompanyDTO getCompanyById(UUID companyId) {
        log.warn("Get company by id cache missing");
        try {
            return authorizedClient
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

    public void setToken(String token) {
        authorizedClient = coreClient
                .mutate()
                .defaultHeader("Authorization", "Bearer " + token)
                .build();
    }
}
