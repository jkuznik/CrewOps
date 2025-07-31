package pl.crewops.infrastructure.core;

import static pl.crewops.enums.ControllerURL.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
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
import pl.crewops.exceptions.NotAuthenticatedException;
import pl.crewops.registration.CreateCustomerCommand;
import pl.crewops.registration.CreateCustomerResult;

@Slf4j
@RequiredArgsConstructor
class CoreClient implements CoreAPI {

    private final RestClient coreClient;
    private RestClient authorizedClient;

    @Getter
    @Setter
    private boolean authenticated;

    // permit all for sure
    @Override
    public AuthResponse login(AuthRequest authRequest) {
        try {
            return coreClient
                    .post()
                    .uri(uriBuilder -> uriBuilder.path(LOGIN).build())
                    .body(authRequest)
                    .retrieve()
                    .body(new ParameterizedTypeReference<AuthResponse>() {});
        } catch (RestClientException e) {
            log.error("Login failed");
            e.printStackTrace();
            throw e;
        }
    }

    // permit all or authenticated on fe side?
    @Override
    public Optional<ValidTokenResponse> validateToken(ValidTokenRequest validTokenRequest) {
        try {
            log.debug("Validating token start");
            ValidTokenResponse body = coreClient
                    .post()
                    .uri(uriBuilder -> uriBuilder.path(VALIDATE).build())
                    .body(validTokenRequest)
                    .retrieve()
                    .body(new ParameterizedTypeReference<ValidTokenResponse>() {});
            log.debug("Validated token: {}", body);
            return Optional.ofNullable(body);
        } catch (RestClientException e) {
            log.error("Validation failed");
            return Optional.empty();
        }
    }

    // manager permission
    @Override
    @CacheEvict(value = "employeeCache", allEntries = true)
    public Optional<EmployeeDTO> createEmployee(CreateEmployeeDTO createEmployeeDTO) throws NotAuthenticatedException {
        isAuthenticated();
        try {
            return Optional.ofNullable(authorizedClient
                    .post()
                    .uri(uriBuilder -> uriBuilder.path(EMPLOYEES).build())
                    .body(createEmployeeDTO)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(new ParameterizedTypeReference<EmployeeDTO>() {}));
        } catch (RestClientException e) {
            log.error("Create new employee error");
            return Optional.empty();
        }
    }

    // TODO: consider about implement security on fe side
    @Override
    public Optional<CreateCustomerResult> registerNewCustomer(CreateCustomerCommand command)
            throws NotAuthenticatedException {
        isAuthenticated();
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
    @Override
    public Optional<EmployeeDTO> updateEmployee(UpdateEmployeeDTO updateEmployeeDTO) throws NotAuthenticatedException {
        isAuthenticated();
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
    @Override
    public Optional<QualificationDTO> updateQualification(UpdateQualificationDTO updateQualificationDTO)
            throws NotAuthenticatedException {
        isAuthenticated();
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
    @Override
    public Optional<QualificationDTO> createQualification(CreateQualificationDTO createQualificationDTO)
            throws NotAuthenticatedException {
        isAuthenticated();
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
    @Override
    public Optional<VehicleDTO> createVehicle(CreateVehicleDTO createVehicleDTO) throws NotAuthenticatedException {
        isAuthenticated();
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
    @Override
    public Optional<VehicleDTO> updateVehicle(UpdateVehicleDTO updateVehicleDTO) throws NotAuthenticatedException {
        isAuthenticated();
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
    @Override
    public Optional<BreakdownDTO> createBreakdown(CreateBreakdownDTO createBreakdownDTO)
            throws NotAuthenticatedException {
        isAuthenticated();
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
    @Override
    public Optional<BreakdownDTO> updateBreakdown(UpdateBreakdownDTO updateBreakdownDTO)
            throws NotAuthenticatedException {
        isAuthenticated();
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
    @Override
    @Cacheable(cacheNames = "employeesCache")
    public List<EmployeeDTO> getAllEmployees() throws NotAuthenticatedException {
        isAuthenticated();
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
    @Override
    public Optional<CompanyDTO> getCompanyById(UUID companyId) throws NotAuthenticatedException {
        log.info("Call getCompanyById");
        isAuthenticated();
        try {
            return authorizedClient
                    .get()
                    .uri(uriBuilder -> uriBuilder.path(COMPANIES_CID).build(companyId))
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(new ParameterizedTypeReference<Optional<CompanyDTO>>() {});
        } catch (RestClientException e) {
            log.error("Error getting company by id");
            return Optional.empty();
        }
    }

    // authenticated
    @Override
    @Cacheable(cacheNames = "qualificationsCache")
    public List<QualificationDTO> getAllQualifications() throws NotAuthenticatedException {
        isAuthenticated();
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
    @Override
    public Optional<EmployeeDTO> getEmployeeById(UUID employeeId) throws NotAuthenticatedException {
        isAuthenticated();
        try {
            return authorizedClient
                    .get()
                    .uri(uriBuilder -> uriBuilder.path(EMPLOYEES_EID).build(employeeId))
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(new ParameterizedTypeReference<Optional<EmployeeDTO>>() {});
        } catch (RestClientException e) {
            log.error("Error getting employee by id");
            return Optional.empty();
        }
    }

    // authenticated
    @Override
    public List<VehicleDTO> getAllVehicles() throws NotAuthenticatedException {
        isAuthenticated();
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
    @Override
    public List<VehicleTypeDTO> getAllVehicleTypes() throws NotAuthenticatedException {
        isAuthenticated();
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
    @Override
    public List<BreakdownDTO> getAllBreakdowns() throws NotAuthenticatedException {
        isAuthenticated();
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
    @Override
    public void terminateEmployeeAccount(UUID employeeId) throws NotAuthenticatedException {
        isAuthenticated();
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
    @Override
    public void deleteQualification(UUID qualificationId) throws NotAuthenticatedException {
        isAuthenticated();
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
    @Override
    public void deleteVehicle(UUID vehicleId) throws NotAuthenticatedException {
        isAuthenticated();
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

    @Override
    public void setToken(String token) {
        authorizedClient = coreClient
                .mutate()
                .defaultHeader("Authorization", "Bearer " + token)
                .build();
    }

    @Override
    public void setAuthentication(boolean authenticated) {
        this.authenticated = authenticated;
    }

    private void isAuthenticated() throws NotAuthenticatedException {
        if (!authenticated) {
            throw new NotAuthenticatedException();
        }
    }
}
