package pl.crewops.infrastructure.core;

import static pl.crewops.enums.ControllerURL.*;
import static pl.crewops.util.CacheResolver.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClientException;
import pl.crewops.exceptions.NotAuthenticatedException;
import pl.crewops.model.dto.machine.CreateMachineDTO;
import pl.crewops.model.dto.machine.MachineDTO;
import pl.crewops.model.dto.machine.UpdateMachineDTO;
import pl.crewops.model.dto.machineType.MachineTypeDTO;

@Slf4j
class DomainMachineClient {

    private final AuthorizationProvider authorizationProvider;

    public DomainMachineClient(AuthorizationProvider authorizationProvider) {
        this.authorizationProvider = authorizationProvider;
    }

    // manager permission
    public MachineDTO createMachine(CreateMachineDTO createMachineDTO) throws NotAuthenticatedException {
        try {
            return authorizationProvider
                    .authorizedClient()
                    .post()
                    .uri(uriBuilder -> uriBuilder.path(MACHINES).build())
                    .body(createMachineDTO)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (RestClientException e) {
            log.error("Create new employee error", e);
            return null;
        }
    }

    // authenticated
    public List<MachineDTO> getAllMachines() throws NotAuthenticatedException {
        log.info("Get all machines cache missed");
        try {
            return authorizationProvider
                    .authorizedClient()
                    .get()
                    .uri(uriBuilder -> uriBuilder.path(MACHINES).build())
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<MachineDTO>>() {});
        } catch (RestClientException e) {
            log.error("Error getting machines");
            return List.of();
        }
    }

    // authenticated
    public List<MachineTypeDTO> getAllMachineTypes() throws NotAuthenticatedException {
        log.info("Get all machine types cache missed");
        try {
            return authorizationProvider
                    .authorizedClient()
                    .get()
                    .uri(uriBuilder -> uriBuilder.path(MACHINE_TYPES).build())
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<MachineTypeDTO>>() {});
        } catch (RestClientException e) {
            log.error("Error getting machine types");
            return List.of();
        }
    }

    // manager permission
    public List<MachineDTO> getAllEmployeeMachinesByIds(Set<UUID> ids) throws NotAuthenticatedException {
        try {
            return authorizationProvider
                    .authorizedClient()
                    .post()
                    .uri(uriBuilder -> uriBuilder.path(MACHINES_VIDS).build())
                    .body(ids)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (RestClientException e) {
            log.error("Get all employee machine ids error");
            return List.of();
        }
    }

    // manager permission or mechanic authority?

    // shift leader or mechanic
    public MachineDTO updateMachine(UpdateMachineDTO updateMachineDTO) throws NotAuthenticatedException {
        try {
            return authorizationProvider
                    .authorizedClient()
                    .patch()
                    .uri(uriBuilder -> uriBuilder.path(MACHINES_VID).build(updateMachineDTO.machineId()))
                    .body(updateMachineDTO)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (RestClientException e) {
            log.error("Update employee error", e);
            return null;
        }
    }

    // manager permission
    public void deleteMachine(UUID machineId) throws NotAuthenticatedException {
        try {
            authorizationProvider
                    .authorizedClient()
                    .delete()
                    .uri(uriBuilder -> uriBuilder
                            .path(MACHINES_VID.replace("{" + MACHINE_ID + "}", machineId.toString()))
                            .build())
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException e) {
            log.error("Error deleting machine", e);
        }
    }
}
