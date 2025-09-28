package pl.crewops.domain.option;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import pl.crewops.domain.auth.AuthAPI;

@RestController
@RequiredArgsConstructor
class OptionController {

    private final AuthAPI authAPI;
}
