package pl.crewops.security.jwt;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import pl.crewops.auth.AuthResponse;
import pl.crewops.auth.ValidTokenRequest;
import pl.crewops.auth.ValidTokenResponse;
import pl.crewops.infrastructure.core.CoreAPI;

@Component
@Getter
@Setter
public class JwtInfoService {

    private final CoreAPI coreAPI;

    private String token;
    private String firstName;
    private String lastName;
    private Date expires;

    public JwtInfoService(CoreAPI coreAPI) {
        this.coreAPI = coreAPI;
    }

    public void setAuthentication(AuthResponse authResponse) {
        token = authResponse.token();
        coreAPI.setToken(authResponse.token());
    }

    public void resetAuthentication() {
        notAuthenticated();
    }

    public boolean validToken() {
        if (token == null) {
            return false;
        }
        var validTokenResponse = coreAPI.validateToken(new ValidTokenRequest(token));

        return validTokenResponse.valid() ? authenticated(validTokenResponse) : notAuthenticated();
    }

    private boolean authenticated(ValidTokenResponse validTokenResponse) {
        firstName = validTokenResponse.employeeDTO().firstName();
        lastName = validTokenResponse.employeeDTO().lastName();
        expires = validTokenResponse.expiration();
        return true;
    }

    private boolean notAuthenticated() {
        token = null;
        firstName = null;
        lastName = null;
        expires = null;

        coreAPI.resetToken();
        return false;
    }
}
