package pl.crewops.infrastructure.core;

import static pl.crewops.enums.ControllerURL.*;

import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClientException;
import pl.crewops.exceptions.NotAuthenticatedException;
import pl.crewops.model.dto.employee.EmployeeDTO;
import pl.crewops.model.dto.qualification.CreateQualificationDTO;
import pl.crewops.model.dto.qualification.QualificationDTO;
import pl.crewops.model.dto.qualification.UpdateQualificationDTO;
import pl.crewops.model.dto.qualification.UpdateQualificationExpiredAtDTO;

@Slf4j
class DomainQualificationClient {

    private final AuthorizationProvider authorizationProvider;

    public DomainQualificationClient(AuthorizationProvider authorizationProvider) {
        this.authorizationProvider = authorizationProvider;
    }

    // manager permission
    public QualificationDTO createQualification(CreateQualificationDTO createQualificationDTO)
            throws NotAuthenticatedException {
        try {
            return authorizationProvider
                    .authorizedClient()
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

    // authenticated
    public List<QualificationDTO> getAllQualifications() throws NotAuthenticatedException {
        log.info("Get all qualifications cache missed");
        try {
            return authorizationProvider
                    .authorizedClient()
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
    public List<QualificationDTO> getAllQualificationsWithExpirationTimeByEmployeeId(UUID employeeId)
            throws NotAuthenticatedException {
        try {
            return authorizationProvider
                    .authorizedClient()
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

    // manager permission
    public QualificationDTO updateQualification(UpdateQualificationDTO updateQualificationDTO)
            throws NotAuthenticatedException {
        try {
            return authorizationProvider
                    .authorizedClient()
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
    public EmployeeDTO updateQualificationExpireAt(UpdateQualificationExpiredAtDTO updateQualificationExpiredAtDTO)
            throws NotAuthenticatedException {
        try {
            return authorizationProvider
                    .authorizedClient()
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
    public void deleteQualification(UUID qualificationId) throws NotAuthenticatedException {
        try {
            authorizationProvider
                    .authorizedClient()
                    .delete()
                    .uri(uriBuilder -> uriBuilder.path(QUALIFICATIONS_QID).build(qualificationId))
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException e) {
            log.error("Error deleting qualification", e);
        }
    }
}
