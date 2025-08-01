package pl.crewops.infrastructure.core;

import static pl.crewops.enums.ControllerURL.*;
import static pl.crewops.util.CacheResolver.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
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
import pl.crewops.dto.qualification.CreateQualificationDTO;
import pl.crewops.dto.qualification.QualificationDTO;
import pl.crewops.dto.qualification.UpdateQualificationDTO;
import pl.crewops.dto.vehicle.CreateVehicleDTO;
import pl.crewops.dto.vehicle.UpdateVehicleDTO;
import pl.crewops.dto.vehicle.VehicleDTO;
import pl.crewops.dto.vehicleType.VehicleTypeDTO;
import pl.crewops.registration.CreateCustomerCommand;
import pl.crewops.registration.CreateCustomerResult;

@Slf4j
@RequiredArgsConstructor
class CoreClient {

    private final RestClient coreClient;
    private RestClient authorizedClient;

    // permit all for sure
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

    @CacheEvict(value = GET_ALL_EMPLOYEES, key = "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()")
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

    public Optional<CreateCustomerResult> registerNewCustomer(CreateCustomerCommand command) {

        try {
            return Optional.ofNullable(authorizedClient
                    .post()
                    .uri(uriBuilder -> uriBuilder.path(REGISTER).build())
                    .body(command)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(new ParameterizedTypeReference<CreateCustomerResult>() {}));
        } catch (RestClientException e) {
            log.error("Create new customer error");
            return Optional.empty();
        }
    }

    // manager permission

    public Optional<EmployeeDTO> updateEmployee(UpdateEmployeeDTO updateEmployeeDTO) {

        try {
            return Optional.ofNullable(authorizedClient
                    .patch()
                    .uri(uriBuilder -> uriBuilder.path(EMPLOYEES_EID).build(updateEmployeeDTO.employeeId()))
                    .body(updateEmployeeDTO)
                    .retrieve()
                    .body(new ParameterizedTypeReference<EmployeeDTO>() {}));
        } catch (RestClientException e) {
            log.error("Update employee error");
            return Optional.empty();
        }
    }

    // manager permission

    public Optional<QualificationDTO> updateQualification(UpdateQualificationDTO updateQualificationDTO) {

        try {
            return Optional.ofNullable(authorizedClient
                    .patch()
                    .uri(uriBuilder ->
                            uriBuilder.path(QUALIFICATIONS_QID).build(updateQualificationDTO.qualificationId()))
                    .body(updateQualificationDTO)
                    .retrieve()
                    .body(new ParameterizedTypeReference<QualificationDTO>() {}));
        } catch (RestClientException e) {
            log.error("Update qualification error");
            return Optional.empty();
        }
    }

    // manager permission

    public Optional<QualificationDTO> createQualification(CreateQualificationDTO createQualificationDTO) {

        try {
            return Optional.ofNullable(authorizedClient
                    .post()
                    .uri(uriBuilder -> uriBuilder.path(QUALIFICATIONS).build())
                    .body(createQualificationDTO)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(new ParameterizedTypeReference<QualificationDTO>() {}));
        } catch (RestClientException e) {
            log.error("Create new qualification error");
            return Optional.empty();
        }
    }

    // manager permission or mechanic authority?

    public Optional<VehicleDTO> createVehicle(CreateVehicleDTO createVehicleDTO) {

        try {
            return Optional.ofNullable(authorizedClient
                    .post()
                    .uri(uriBuilder -> uriBuilder.path(VEHICLES).build())
                    .body(createVehicleDTO)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(new ParameterizedTypeReference<VehicleDTO>() {}));
        } catch (RestClientException e) {
            log.error("Create new employee error", e);
            return Optional.empty();
        }
    }

    // shift leader or mechanic

    public Optional<VehicleDTO> updateVehicle(UpdateVehicleDTO updateVehicleDTO) {

        try {
            return Optional.ofNullable(authorizedClient
                    .patch()
                    .uri(uriBuilder -> uriBuilder.path(VEHICLES_VID).build(updateVehicleDTO.vehicleId()))
                    .body(updateVehicleDTO)
                    .retrieve()
                    .body(new ParameterizedTypeReference<VehicleDTO>() {}));
        } catch (RestClientException e) {
            log.error("Update employee error", e);
            return Optional.empty();
        }
    }

    // authenticated

    public Optional<BreakdownDTO> createBreakdown(CreateBreakdownDTO createBreakdownDTO) {

        try {
            return Optional.ofNullable(authorizedClient
                    .post()
                    .uri(uriBuilder -> uriBuilder.path(BREAKDOWNS).build())
                    .body(createBreakdownDTO)
                    .retrieve()
                    .body(new ParameterizedTypeReference<BreakdownDTO>() {}));
        } catch (RestClientException e) {
            log.error("Create new breakdown error");
            return Optional.empty();
        }
    }

    // shift leader or mechanic

    public Optional<BreakdownDTO> updateBreakdown(UpdateBreakdownDTO updateBreakdownDTO) {

        try {
            return Optional.ofNullable(authorizedClient
                    .patch()
                    .uri(uriBuilder -> uriBuilder.path(BREAKDOWNS_BID).build((updateBreakdownDTO.breakdownId())))
                    .body(updateBreakdownDTO)
                    .retrieve()
                    .body(new ParameterizedTypeReference<BreakdownDTO>() {}));
        } catch (RestClientException e) {
            log.error("Update breakdown error", e);
            return Optional.empty();
        }
    }

    // authenticated

    @Cacheable(cacheNames = GET_ALL_EMPLOYEES, key = "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()")
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

    // authenticated

    @Cacheable(cacheNames = GET_COMPANY_BY_ID, key = "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()")
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

    // authenticated

    @Cacheable(cacheNames = GET_ALL_QUALIFICATIONS, key = "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()")
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

    // authenticated

    @Cacheable(cacheNames = GET_EMPLOYEE_BY_ID, key = "T(pl.crewops.util.CacheResolver).getCurrentCompanyId()")
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

    public List<VehicleDTO> getAllVehicles() {

        try {
            return authorizedClient
                    .get()
                    .uri(uriBuilder -> uriBuilder.path(VEHICLES).build())
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<VehicleDTO>>() {});
        } catch (RestClientException e) {
            log.error("Error getting vehicles");
            return List.of();
        }
    }

    // authenticated

    public List<VehicleTypeDTO> getAllVehicleTypes() {

        try {
            return authorizedClient
                    .get()
                    .uri(uriBuilder -> uriBuilder.path(VEHICLE_TYPES).build())
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<VehicleTypeDTO>>() {});
        } catch (RestClientException e) {
            log.error("Error getting vehicle types");
            return List.of();
        }
    }

    // authenticated

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

    // manager permission

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

    // manager permission

    public void deleteVehicle(UUID vehicleId) {

        try {
            authorizedClient
                    .delete()
                    .uri(uriBuilder -> uriBuilder
                            .path(VEHICLES_VID.replace("{" + VEHICLE_ID + "}", vehicleId.toString()))
                            .build())
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException e) {
            log.error("Error deleting vehicle", e);
        }
    }

    public void setToken(String token) {
        authorizedClient = coreClient
                .mutate()
                .defaultHeader("Authorization", "Bearer " + token)
                .build();
    }
}
