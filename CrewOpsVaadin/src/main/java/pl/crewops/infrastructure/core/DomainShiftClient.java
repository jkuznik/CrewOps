package pl.crewops.infrastructure.core;

import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import pl.crewops.enums.ControllerURL;
import pl.crewops.exceptions.NotAuthenticatedException;
import pl.crewops.model.dto.shift.CreateShiftDTO;
import pl.crewops.model.dto.shift.ShiftDTO;
import pl.crewops.model.dto.shift.UpdateShiftDTO;

@Slf4j
public class DomainShiftClient extends DomainAbstractClient {

    public DomainShiftClient(AuthorizationProvider authorizationProvider) {
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
            log.error("Fail to create shift.");
            return null;
        }
    }

    public List<ShiftDTO> getAllShifts() {
        try {
            return authorizedClient()
                    .get()
                    .uri(uriBuilder -> uriBuilder.path(ControllerURL.SHIFTS).build())
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (NotAuthenticatedException e) {
            log.error("Fail to get all shift.");
            return List.of();
        }
    }

    public ShiftDTO updateShift(UpdateShiftDTO updateShiftDTO) {
        try {
            return authorizedClient()
                    .put()
                    .uri(uriBuilder -> uriBuilder.path(ControllerURL.SHIFTS).build())
                    .body(updateShiftDTO)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (NotAuthenticatedException e) {
            log.error("Fail to update shift.");
            return null;
        }
    }

    public void deleteShift(UUID id) {
        try {
            authorizedClient()
                    .delete()
                    .uri(uriBuilder -> uriBuilder.path(ControllerURL.SHIFTS_SID).build(id))
                    .retrieve()
                    .toBodilessEntity();
        } catch (NotAuthenticatedException e) {
            log.error("Fail to delete shift.");
        }
    }
}
