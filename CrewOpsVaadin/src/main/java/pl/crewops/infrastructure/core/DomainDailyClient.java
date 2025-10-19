package pl.crewops.infrastructure.core;

import static pl.crewops.enums.ControllerURL.DAILY_ENTRIES;

import java.time.LocalDate;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClientException;
import pl.crewops.exceptions.NotAuthenticatedException;
import pl.crewops.model.dto.dailyEntry.CreateDailyEntryDTO;
import pl.crewops.model.dto.dailyEntry.DailyEntryDTO;
import pl.crewops.model.dto.dailyEntry.UpdateDailyEntryCommand;

@Slf4j
class DomainDailyClient extends DomainAbstractClient {

    public DomainDailyClient(AuthorizationProvider authorizationProvider) {
        super(authorizationProvider);
    }

    // authenticated
    public DailyEntryDTO createDailyEntry(CreateDailyEntryDTO createDailyEntryDTO) throws NotAuthenticatedException {
        try {
            return authorizedClient()
                    .post()
                    .uri(uriBuilder -> uriBuilder.path(DAILY_ENTRIES).build())
                    .body(createDailyEntryDTO)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (RestClientException e) {
            log.error("Create new daily entry error");
            return null;
        }
    }

    // authenticated but regular user can fetch only his own daily entry
    public DailyEntryDTO findDailyEntryByEmployeeIdAndDate(UUID employeeId, LocalDate localDate)
            throws NotAuthenticatedException {
        try {
            return authorizedClient()
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path(DAILY_ENTRIES)
                            .queryParam("employeeId", employeeId)
                            .queryParam("entryDate", localDate)
                            .build())
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (RestClientException e) {
            log.warn("Get daily entry by employee id and entry date failed");
            return null;
        }
    }

    // authenticated and only self resources (for managers should be dedicated method)
    public DailyEntryDTO updateDailyEntrySelfPermission(UpdateDailyEntryCommand updateDailyEntryCommand)
            throws NotAuthenticatedException {
        try {
            return authorizedClient()
                    .patch()
                    .uri(uriBuilder -> uriBuilder.path(DAILY_ENTRIES).build())
                    .body(updateDailyEntryCommand)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (RestClientException e) {
            log.error("Update daily entry failed");
            return null;
        }
    }
}
