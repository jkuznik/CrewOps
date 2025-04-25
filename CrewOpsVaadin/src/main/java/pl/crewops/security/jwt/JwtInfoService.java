package pl.crewops.security.jwt;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import pl.crewops.auth.ValidTokenRequest;
import pl.crewops.infrastructure.core.CoreAPI;

@Component
@Getter
@Setter
public class JwtInfoService {

    private String token;
    private String firstName;
    private String lastName;

    private String username;

    private Date expires;

    public boolean validToken(CoreAPI coreApi) {
        if (token == null || username == null) {
            return false;
        }
        return coreApi.validateToken(new ValidTokenRequest(token, username));
    }
}
