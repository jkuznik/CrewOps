package pl.crewops.domain.registration;

import static pl.crewops.enums.ControllerURL.REGISTER;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.crewops.registration.CreateCustomerCommand;
import pl.crewops.registration.PreRegisterResponse;

@RestController
@RequiredArgsConstructor
@Validated
class RegistrationController {

    private final RegistrationService registrationService;

    @PostMapping(REGISTER)
    public ResponseEntity<PreRegisterResponse> registerCustomer(
            @NotNull @Valid @RequestBody CreateCustomerCommand createCustomerCommand) {
        return ResponseEntity.ok(registrationService.preRegisterCustomerEmailValidation(createCustomerCommand));
    }
}
