package pl.crewops.infrastructure.core;

import static pl.crewops.enums.ControllerURL.COMPANIES_CID;

import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClientException;
import pl.crewops.exceptions.NotAuthenticatedException;
import pl.crewops.model.dto.company.CompanyDTO;

@Slf4j
class DomainCompanyClient {
    private final AuthorizationProvider authorizationProvider;

    public DomainCompanyClient(AuthorizationProvider authorizationProvider) {
        this.authorizationProvider = authorizationProvider;
    }

    // authenticated
    public CompanyDTO getCompanyById(UUID companyId) throws NotAuthenticatedException {
        log.info("Get company by id cache missed");
        try {
            return authorizationProvider
                    .authorizedClient()
                    .get()
                    .uri(uriBuilder -> uriBuilder.path(COMPANIES_CID).build(companyId))
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (RestClientException e) {
            log.error("Error getting company by id");
            return null;
        }
    }
}
