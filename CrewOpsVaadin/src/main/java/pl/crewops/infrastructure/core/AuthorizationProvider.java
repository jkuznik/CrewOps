package pl.crewops.infrastructure.core;

import org.springframework.web.client.RestClient;
import pl.crewops.exceptions.NotAuthenticatedException;
import pl.crewops.util.AuthenticationResolver;
import pl.crewops.util.SpringContextBridge;

class AuthorizationProvider {

    private final RestClient coreClient;

    public AuthorizationProvider(RestClient coreClient) {
        this.coreClient = coreClient;
    }

    public RestClient authorizedClient() throws NotAuthenticatedException {
        AuthenticationResolver authenticationResolver = SpringContextBridge.getBean(AuthenticationResolver.class);
        if (authenticationResolver.getPrincipal() != null) {
            return coreClient
                    .mutate()
                    .defaultHeader(
                            "Authorization",
                            "Bearer " + authenticationResolver.getPrincipal().getToken())
                    .build();
        } else {
            throw new NotAuthenticatedException();
        }
    }
}
