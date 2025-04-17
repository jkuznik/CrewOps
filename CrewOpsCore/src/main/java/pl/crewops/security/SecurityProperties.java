package pl.crewops.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "properties.security")
@Getter
@Setter
public class SecurityProperties {

    private String clientId;
    private String jwtSecret;
    private long jwtExpiration;
}
