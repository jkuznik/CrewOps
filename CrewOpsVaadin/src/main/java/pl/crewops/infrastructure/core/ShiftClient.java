package pl.crewops.infrastructure.core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import pl.crewops.enums.ControllerURL;
import pl.crewops.exceptions.NotAuthenticatedException;
import pl.crewops.model.dto.shift.CreateShiftDTO;
import pl.crewops.model.dto.shift.ShiftDTO;

@Slf4j
public class ShiftClient extends DomainAbstractClient {

    public ShiftClient(AuthorizationProvider authorizationProvider) {
        super(authorizationProvider);
    }

    public ShiftDTO createShift(CreateShiftDTO createShiftDTO) {
        try {
            return authorizedClient()
                    .post()
                    .uri(uriBuilder -> uriBuilder.path(ControllerURL.SHIFTS).build())
                    .body(createShiftDTO)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (NotAuthenticatedException e) {
            log.error(e.getMessage());
            return null;
        }
    }
}
