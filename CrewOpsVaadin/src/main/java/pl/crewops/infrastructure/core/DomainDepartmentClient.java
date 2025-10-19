package pl.crewops.infrastructure.core;

import static pl.crewops.enums.ControllerURL.DEPARTMENTS;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClientException;
import pl.crewops.exceptions.NotAuthenticatedException;
import pl.crewops.model.dto.department.DepartmentDTO;

@Slf4j
class DomainDepartmentClient {
    private final AuthorizationProvider authorizationProvider;

    public DomainDepartmentClient(AuthorizationProvider authorizationProvider) {
        this.authorizationProvider = authorizationProvider;
    }

    // authenticated
    public List<DepartmentDTO> getAllDepartments() throws NotAuthenticatedException {
        log.info("Get all departments cache missed");
        try {
            return authorizationProvider
                    .authorizedClient()
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
}
