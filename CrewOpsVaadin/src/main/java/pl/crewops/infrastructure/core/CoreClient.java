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
import pl.crewops.dto.employee.CreateEmployeeDTO;
import pl.crewops.dto.employee.EmployeeDTO;
import pl.crewops.dto.qualification.CreateQualificationDTO;
import pl.crewops.dto.qualification.QualificationDTO;
import pl.crewops.dto.vehicle.VehicleDTO;
import pl.crewops.enums.ControllerURL;

@Slf4j
@RequiredArgsConstructor
class CoreClient implements CoreAPI {

    private final RestClient coreClient;

    public Optional<EmployeeDTO> createEmployee(CreateEmployeeDTO createEmployeeDTO) {
        try {
            return Optional.ofNullable(coreClient
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
            return Optional.ofNullable(coreClient
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

    public List<EmployeeDTO> getAllEmployees() {
        try {
            return coreClient
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
            return coreClient
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

    public List<QualificationDTO> getQualificationsByIds(Set<UUID> qualificationsIds) {
        try {
            return coreClient
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
            return coreClient
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
            coreClient
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
            coreClient
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
