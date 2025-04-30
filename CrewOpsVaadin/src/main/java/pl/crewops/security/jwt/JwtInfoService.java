package pl.crewops.security.jwt;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
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

    public boolean validToken() {
        if (token == null) {
            return false;
        }

        ValidTokenResponse validTokenResponse = coreAPI.validateToken(new ValidTokenRequest(token));
        if (validTokenResponse.valid()) {
            firstName = validTokenResponse.employeeDTO().firstName();
            lastName = validTokenResponse.employeeDTO().lastName();
            expires = validTokenResponse.expiration();
            return true;
        } else {
            return false;
        }
    }
}
