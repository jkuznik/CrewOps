package pl.crewops.domain.registration;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.crewops.dto.CreateCustomerCommand;
import pl.crewops.enums.ControllerURL;

@RestController
@RequiredArgsConstructor
class RegistrationController {

    private final RegistrationService registrationService;

    @PostMapping(ControllerURL.REGISTER)
    public ResponseEntity<Void> registerCustomer(
            @NotNull @Valid @RequestBody CreateCustomerCommand createCustomerCommand) {
        return ResponseEntity.ok(registrationService.registerCustomer(createCustomerCommand));
    }
}
