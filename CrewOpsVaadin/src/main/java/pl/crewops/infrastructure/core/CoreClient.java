package pl.crewops.infrastructure.core;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import pl.crewops.auth.AuthRequest;
import pl.crewops.auth.AuthResponse;
import pl.crewops.auth.ValidTokenRequest;
import pl.crewops.dto.employee.CreateEmployeeDTO;
import pl.crewops.dto.employee.EmployeeDTO;
import pl.crewops.dto.qualification.CreateQualificationDTO;
import pl.crewops.dto.qualification.QualificationDTO;
import pl.crewops.dto.vehicle.CreateVehicleDTO;
import pl.crewops.dto.vehicle.VehicleDTO;
import pl.crewops.enums.ControllerURL;

@Slf4j
@RequiredArgsConstructor
class CoreClient implements CoreAPI {

    private final RestClient coreClient;
    private RestClient authorizedClient;

    @Override
    public AuthResponse login(AuthRequest authRequest) {
        try {
            return coreClient
                    .post()
                    .uri(uriBuilder -> uriBuilder.path(ControllerURL.LOGIN).build())
                    .body(authRequest)
                    .retrieve()
                    .body(new ParameterizedTypeReference<AuthResponse>() {});
        } catch (RestClientException e) {
            log.error("Login failed", e);
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public void setToken(AuthResponse response) {
        this.authorizedClient = coreClient
                .mutate()
                .defaultHeader("Authorization", "Bearer " + response.token())
                .build();
    }

    @Override
    public Boolean validateToken(ValidTokenRequest validTokenRequest) {
        try {
            return coreClient
                    .post()
                    .uri(uriBuilder -> uriBuilder.path(ControllerURL.VALIDATE).build())
                    .body(validTokenRequest)
                    .retrieve()
                    .body(new ParameterizedTypeReference<Boolean>() {});
        } catch (RestClientException e) {
            log.error("Validation failed", e);
            e.printStackTrace();
            throw e;
        }
    }

    public Optional<EmployeeDTO> createEmployee(CreateEmployeeDTO createEmployeeDTO) {
        try {
            return Optional.ofNullable(authorizedClient
                    .post()
                    .uri(uriBuilder -> uriBuilder.path(ControllerURL.EMPLOYEES).build())
                    .body(createEmployeeDTO)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(new ParameterizedTypeReference<EmployeeDTO>() {}));
        } catch (RestClientException e) {
            log.error("Create new employee error", e);
            return Optional.empty();
        }
    }

    public Optional<QualificationDTO> createQualification(CreateQualificationDTO createQualificationDTO) {
        try {
            return Optional.ofNullable(authorizedClient
                    .post()
                    .uri(uriBuilder ->
                            uriBuilder.path(ControllerURL.QUALIFICATIONS).build())
                    .body(createQualificationDTO)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(new ParameterizedTypeReference<QualificationDTO>() {}));
        } catch (RestClientException e) {
            log.error("Create new employee error", e);
            return Optional.empty();
        }
    }

    public Optional<VehicleDTO> createVehicle(CreateVehicleDTO createVehicleDTO) {
        try {
            return Optional.ofNullable(authorizedClient
                    .post()
                    .uri(uriBuilder -> uriBuilder.path(ControllerURL.VEHICLES).build())
                    .body(createVehicleDTO)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(new ParameterizedTypeReference<VehicleDTO>() {}));
        } catch (RestClientException e) {
            log.error("Create new employee error", e);
            return Optional.empty();
        }
    }

    public List<EmployeeDTO> getAllEmployees() {
        try {
            return authorizedClient
                    .get()
                    .uri(uriBuilder -> uriBuilder.path(ControllerURL.EMPLOYEES).build())
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<EmployeeDTO>>() {});
        } catch (RestClientException e) {
            log.error("Error getting employees", e);
            return List.of();
        }
    }

    public List<QualificationDTO> getAllQualifications() {
        try {
            return authorizedClient
                    .get()
                    .uri(uriBuilder ->
                            uriBuilder.path(ControllerURL.QUALIFICATIONS).build())
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<QualificationDTO>>() {});
        } catch (RestClientException e) {
            log.error("Error getting employees", e);
            return List.of();
        }
    }

    public List<VehicleDTO> getAllVehicles() {
        try {
            return authorizedClient
                    .get()
                    .uri(uriBuilder -> uriBuilder.path(ControllerURL.VEHICLES).build())
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<VehicleDTO>>() {});
        } catch (RestClientException e) {
            log.error("Error getting vehicles", e);
            return List.of();
        }
    }

    public List<QualificationDTO> getQualificationsByIds(Set<UUID> qualificationsIds) {
        try {
            return authorizedClient
                    .post()
                    .uri(uriBuilder ->
                            uriBuilder.path(ControllerURL.QUALIFICATIONS_QIDS).build())
                    .body(qualificationsIds)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<QualificationDTO>>() {});
        } catch (RestClientException e) {
            log.error("Error getting qualifications", e);
            return List.of();
        }
    }

    public List<VehicleDTO> getVehiclesByIds(Set<UUID> vehiclesIds) {
        try {
            return authorizedClient
                    .post()
                    .uri(uriBuilder ->
                            uriBuilder.path(ControllerURL.VEHICLES_VIDS).build())
                    .body(vehiclesIds)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<VehicleDTO>>() {});
        } catch (RestClientException e) {
            log.error("Error getting qualifications", e);
            return List.of();
        }
    }

    public void deleteEmployee(UUID employeeId) {
        try {
            authorizedClient
                    .delete()
                    .uri(uriBuilder -> uriBuilder
                            .path(ControllerURL.EMPLOYEES_EID.replace(
                                    "{" + ControllerURL.EMPLOYEE_ID + "}", employeeId.toString()))
                            .build())
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException e) {
            log.error("Error deleting employee", e);
        }
    }

    public void deleteQualification(UUID qualificationId) {
        try {
            authorizedClient
                    .delete()
                    .uri(uriBuilder -> uriBuilder
                            .path(ControllerURL.QUALIFICATIONS_QID.replace(
                                    "{" + ControllerURL.QUALIFICATION_ID + "}", qualificationId.toString()))
                            .build())
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException e) {
            log.error("Error deleting qualification", e);
        }
    }

    public void deleteVehicle(UUID vehicleId) {
        try {
            coreClient
                    .delete()
                    .uri(uriBuilder -> uriBuilder
                            .path(ControllerURL.VEHICLES_VID.replace(
                                    "{" + ControllerURL.VEHICLE_ID + "}", vehicleId.toString()))
                            .build())
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException e) {
            log.error("Error deleting vehicle", e);
        }
    }
}
