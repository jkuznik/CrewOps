package pl.crewops.security.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class JwtService {

    private String token;
}
