package pl.crewops.infrastructure.core;

import static pl.crewops.enums.ControllerURL.BREAKDOWNS;
import static pl.crewops.enums.ControllerURL.BREAKDOWNS_BID;
import static pl.crewops.util.CacheResolver.*;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClientException;
import pl.crewops.exceptions.NotAuthenticatedException;
import pl.crewops.model.dto.breakdown.BreakdownDTO;
import pl.crewops.model.dto.breakdown.CreateBreakdownDTO;
import pl.crewops.model.dto.breakdown.UpdateBreakdownDTO;

@Slf4j
class DomainBreakdownClient extends DomainAbstractClient {

    public DomainBreakdownClient(AuthorizationProvider authorizationProvider) {
        super(authorizationProvider);
    }

    // authenticated
    public BreakdownDTO createBreakdown(CreateBreakdownDTO createBreakdownDTO) throws NotAuthenticatedException {
        try {
            return authorizedClient()
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

    // authenticated
    public List<BreakdownDTO> getAllBreakdowns() throws NotAuthenticatedException {
        log.info("Get all breakdowns cache missed");
        try {
            return authorizedClient()
                    .get()
                    .uri(uriBuilder -> uriBuilder.path(BREAKDOWNS).build())
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (RestClientException e) {
            log.error("Error getting breakdowns");
            return List.of();
        }
    }

    // shift leader or mechanic
    public BreakdownDTO updateBreakdown(UpdateBreakdownDTO updateBreakdownDTO) throws NotAuthenticatedException {
        try {
            return authorizedClient()
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
}
