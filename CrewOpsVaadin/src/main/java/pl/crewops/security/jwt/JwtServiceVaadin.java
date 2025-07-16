package pl.crewops.security.jwt;

import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.crewops.auth.ValidTokenRequest;
import pl.crewops.auth.ValidTokenResponse;
import pl.crewops.infrastructure.core.CoreAPI;
import pl.crewops.security.config.SecurityConfigProperties;

@Slf4j
@Service
public class JwtServiceVaadin extends JwtClaimsProvider {

    private final CoreAPI coreAPI;

    public JwtServiceVaadin(SecurityConfigProperties securityConfigProperties, CoreAPI coreAPI) {
        super(securityConfigProperties);
        this.coreAPI = coreAPI;
    }

    public boolean validToken(String token) {
        if (token == null) {
            return false;
        }
        var validTokenResponse =
                coreAPI.validateToken(new ValidTokenRequest(token)).orElse(new ValidTokenResponse(false, new Date()));
        return validTokenResponse.valid();
    }
}
