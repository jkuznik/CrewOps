package pl.crewops.infrastructure.core;

import static pl.crewops.enums.ControllerURL.NOTES;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClientException;
import pl.crewops.exceptions.NotAuthenticatedException;
import pl.crewops.model.dto.note.CreateNoteDTO;
import pl.crewops.model.dto.note.FetchNotesRequest;
import pl.crewops.model.dto.note.NoteDTO;

@Slf4j
public class DomainNoteClient extends DomainAbstractClient {

    public DomainNoteClient(AuthorizationProvider authorizationProvider) {
        super(authorizationProvider);
    }

    // authenticated
    public NoteDTO createDailyNote(CreateNoteDTO createNoteDTO) throws NotAuthenticatedException {
        try {
            return authorizedClient()
                    .post()
                    .uri(uriBuilder -> uriBuilder.path(NOTES).build())
                    .body(createNoteDTO)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (RestClientException e) {
            log.error("Create new daily note error");
            return null;
        }
    }

    // authenticated
    public List<NoteDTO> getAllPublicAndPrincipalPrivateNotesByDate(FetchNotesRequest fetchNotesRequest)
            throws NotAuthenticatedException {
        try {
            return authorizedClient()
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path(NOTES)
                            .queryParam("employeeId", fetchNotesRequest.employeeId())
                            .queryParam("date", fetchNotesRequest.date())
                            .build())
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (RestClientException e) {
            log.error("Get daily note error for date {}", fetchNotesRequest.date(), e);
            return null;
        }
    }
}
