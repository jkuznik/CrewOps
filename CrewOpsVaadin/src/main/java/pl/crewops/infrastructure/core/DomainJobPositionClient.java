package pl.crewops.infrastructure.core;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClientException;
import pl.crewops.enums.ControllerURL;
import pl.crewops.exceptions.NotAuthenticatedException;
import pl.crewops.model.dto.jobPosition.JobPositionDTO;

@Slf4j
class DomainJobPositionClient extends DomainAbstractClient {

    public DomainJobPositionClient(AuthorizationProvider authorizationProvider) {
        super(authorizationProvider);
    }

    // authenticated
    public List<JobPositionDTO> getAllJobPositions() throws NotAuthenticatedException {
        try {
            return authorizedClient()
                    .get()
                    .uri(uriBuilder ->
                            uriBuilder.path(ControllerURL.JOB_POSITIONS).build())
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (RestClientException e) {
            log.error("Get all job positions failed");
            return List.of();
        }
    }
}
