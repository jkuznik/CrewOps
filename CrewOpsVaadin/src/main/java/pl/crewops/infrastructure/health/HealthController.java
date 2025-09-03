package pl.crewops.infrastructure.health;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.crewops.enums.ControllerURL;

@RestController
public class HealthController {

    @GetMapping(ControllerURL.HEALTH)
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("OK");
    }
}
