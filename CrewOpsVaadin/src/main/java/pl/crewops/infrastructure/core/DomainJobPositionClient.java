package pl.crewops.infrastructure.core;

import static pl.crewops.enums.ControllerURL.*;

import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClientException;
import pl.crewops.exceptions.NotAuthenticatedException;
import pl.crewops.model.dto.jobPosition.CreateJobPositionDTO;
import pl.crewops.model.dto.jobPosition.JobPositionDTO;
import pl.crewops.model.dto.jobPosition.UpdateJobPositionDTO;

@Slf4j
class DomainJobPositionClient extends DomainAbstractClient {

    public DomainJobPositionClient(AuthorizationProvider authorizationProvider) {
        super(authorizationProvider);
    }

    // manager permission
    public JobPositionDTO createJobPosition(CreateJobPositionDTO createJobPositionDTO)
            throws NotAuthenticatedException {
        try {
            return authorizedClient()
                    .post()
                    .uri(uriBuilder -> uriBuilder.path(JOB_POSITIONS).build())
                    .body(createJobPositionDTO)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (RestClientException e) {
            log.error("Create job position failed");
            return null;
        }
    }

    // authenticated
    public List<JobPositionDTO> getAllJobPositions() throws NotAuthenticatedException {
        try {
            return authorizedClient()
                    .get()
                    .uri(uriBuilder -> uriBuilder.path(JOB_POSITIONS).build())
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (RestClientException e) {
            log.error("Get all job positions failed");
            return List.of();
        }
    }

    // manager permission
    public JobPositionDTO updateJobPosition(UpdateJobPositionDTO updateJobPositionDTO)
            throws NotAuthenticatedException {
        try {
            return authorizedClient()
                    .patch()
                    .uri(uriBuilder -> uriBuilder.path(JOB_POSITIONS).build())
                    .body(updateJobPositionDTO)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (RestClientException e) {
            log.error("Update job position failed");
            return null;
        }
    }

    // manager permission
    public void deleteById(UUID id) throws NotAuthenticatedException {
        try {
            authorizedClient()
                    .delete()
                    .uri(uriBuilder -> uriBuilder.path(JOB_POSITIONS_JID).build(id))
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException e) {
            log.error("Delete job position failed");
        }
    }
}
