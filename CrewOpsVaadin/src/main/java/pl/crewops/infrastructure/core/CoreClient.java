package pl.crewops.infrastructure.core;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import pl.crewops.dto.employee.EmployeeDTO;
import pl.crewops.dto.qualification.QualificationDTO;
import pl.crewops.enums.ControllerURL;

@Slf4j
@RequiredArgsConstructor
class CoreClient implements CoreAPI {

    private final RestClient coreClient;

    public List<EmployeeDTO> getEmployees() {
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

    public List<QualificationDTO> getQualifications(Set<UUID> qualificationsIds) {
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
}
