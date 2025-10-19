package pl.crewops.security.jwt;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.crewops.security.config.SecurityConfigProperties;

@Slf4j
@Service
public class JwtServiceVaadin extends JwtClaimsProvider {

    public JwtServiceVaadin(SecurityConfigProperties securityConfigProperties) {
        super(securityConfigProperties);
    }
}
