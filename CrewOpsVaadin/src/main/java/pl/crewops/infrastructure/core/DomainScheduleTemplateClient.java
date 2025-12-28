package pl.crewops.infrastructure.core;

import static pl.crewops.enums.ControllerURL.SCHEDULE;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClientException;
import pl.crewops.exceptions.NotAuthenticatedException;
import pl.crewops.model.dto.scheduleTemplate.CreateScheduleTemplateDTO;
import pl.crewops.model.dto.scheduleTemplate.ScheduleTemplateDTO;

@Slf4j
class DomainScheduleTemplateClient extends DomainAbstractClient {

    public DomainScheduleTemplateClient(AuthorizationProvider authorizationProvider) {
        super(authorizationProvider);
    }

    public ScheduleTemplateDTO createScheduleTemplate(CreateScheduleTemplateDTO createScheduleTemplateDTO)
            throws NotAuthenticatedException {
        try {
            return authorizedClient()
                    .post()
                    .uri(uriBuilder -> uriBuilder.path(SCHEDULE).build())
                    .body(createScheduleTemplateDTO)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (RestClientException e) {
            log.error("Error during create schedule template");
            return null;
        }
    }

    public List<ScheduleTemplateDTO> getAllTemplates() throws NotAuthenticatedException {
        try {
            return authorizedClient()
                    .get()
                    .uri(uriBuilder -> uriBuilder.path(SCHEDULE).build())
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (RestClientException e) {
            log.error("Error during get all schedule templates");
            return List.of();
        }
    }
}
