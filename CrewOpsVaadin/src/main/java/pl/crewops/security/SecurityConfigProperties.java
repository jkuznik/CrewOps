package pl.crewops.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "security.properties")
@Getter
@Setter
public class SecurityConfigProperties {

    private String clientId;
}
