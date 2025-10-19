package pl.crewops.infrastructure.core;

import org.springframework.web.client.RestClient;
import pl.crewops.exceptions.NotAuthenticatedException;

abstract class DomainAbstractClient {

    private final AuthorizationProvider authorizationProvider;

    public DomainAbstractClient(AuthorizationProvider authorizationProvider) {
        this.authorizationProvider = authorizationProvider;
    }

    protected RestClient authorizedClient() throws NotAuthenticatedException {
        return authorizationProvider.authorizedClient();
    }
}
