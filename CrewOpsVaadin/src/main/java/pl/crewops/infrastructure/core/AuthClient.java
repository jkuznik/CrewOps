package pl.crewops.infrastructure.core;

import static pl.crewops.enums.ControllerURL.*;

import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import pl.crewops.exceptions.NotAuthenticatedException;
import pl.crewops.model.dto.auth.AuthRequest;
import pl.crewops.model.dto.auth.AuthResponse;
import pl.crewops.model.dto.auth.AuthUserDTO;
import pl.crewops.model.dto.auth.UpdateAuthUserDTO;
import pl.crewops.model.dto.registration.CreateCustomerCommand;
import pl.crewops.model.dto.registration.CreateCustomerResult;
import pl.crewops.model.dto.registration.PreRegisterResponse;
import pl.crewops.model.dto.registration.VerifyEmailRequest;

@Slf4j
class AuthClient {

    private final RestClient coreClient;
    private final AuthorizationProvider authorizationProvider;

    public AuthClient(RestClient coreClient, AuthorizationProvider authorizationProvider) {
        this.coreClient = coreClient;
        this.authorizationProvider = authorizationProvider;
    }

    public PreRegisterResponse registerNewCustomer(CreateCustomerCommand command) {
        try {
            return coreClient
                    .post()
                    .uri(uriBuilder -> uriBuilder.path(REGISTER).build())
                    .body(command)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (RestClientException e) {
            log.error("Create new customer error");
            return null;
        }
    }

    // permit all for sure
    public AuthResponse login(AuthRequest authRequest) {
        try {
            return coreClient
                    .post()
                    .uri(uriBuilder -> uriBuilder.path(LOGIN).build())
                    .body(authRequest)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (RestClientException e) {
            log.error("Login failed" + e.getMessage());
            throw e;
        }
    }

    public CreateCustomerResult verifyEmail(VerifyEmailRequest request) {
        try {
            return coreClient
                    .post()
                    .uri(uriBuilder -> uriBuilder.path(VERIFY_EMAIL).build())
                    .body(request)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (RestClientException e) {
            log.error("Verify email error");
            return null;
        }
    }

    // manager permission
    public AuthUserDTO updateAuthUserRoles(UpdateAuthUserDTO updateAuthUserDTO) throws NotAuthenticatedException {
        try {
            return authorizationProvider
                    .authorizedClient()
                    .patch()
                    .uri(uriBuilder -> uriBuilder.path(UPDATE_USER_ROLES).build())
                    .body(updateAuthUserDTO)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (RestClientException e) {
            log.error("Update auth user failed");
            return null;
        }
    }

    // authenticated BUT only own data
    public AuthUserDTO updateAuthUserCredentials(UpdateAuthUserDTO updateAuthUserDTO) throws NotAuthenticatedException {
        try {
            return authorizationProvider
                    .authorizedClient()
                    .patch()
                    .uri(uriBuilder -> uriBuilder.path(UPDATE_USER_CREDENTIALS).build())
                    .body(updateAuthUserDTO)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (RestClientException e) {
            log.error("Update auth user failed");
            return null;
        }
    }

    // manager permission
    public void terminateEmployeeAccount(UUID employeeId) throws NotAuthenticatedException {
        try {
            authorizationProvider
                    .authorizedClient()
                    .delete()
                    .uri(uriBuilder -> uriBuilder
                            .path(EMPLOYEES_EID.replace("{" + EMPLOYEE_ID + "}", employeeId.toString()))
                            .build())
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException e) {
            log.error("Error deleting employee", e);
        }
    }
}
