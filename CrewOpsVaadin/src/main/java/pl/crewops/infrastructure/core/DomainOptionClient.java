package pl.crewops.infrastructure.core;

import static pl.crewops.enums.ControllerURL.EMPLOYEE_EID_OPTIONS;

import java.util.Set;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClientException;
import pl.crewops.exceptions.NotAuthenticatedException;
import pl.crewops.model.dto.option.AuthUserOptionDTO;

@Slf4j
class DomainOptionClient extends DomainAbstractClient {

    public DomainOptionClient(AuthorizationProvider authorizationProvider) {
        super(authorizationProvider);
    }

    public Set<AuthUserOptionDTO> getOptionsByEmployeeId(UUID employeeId) throws NotAuthenticatedException {
        try {
            return authorizedClient()
                    .get()
                    .uri(uriBuilder -> uriBuilder.path(EMPLOYEE_EID_OPTIONS).build(employeeId))
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (RestClientException e) {
            log.error("Get options by employee failed");
            return Set.of();
        }
    }
}
