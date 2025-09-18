package pl.crewops.domain.registration;

import static pl.crewops.enums.ControllerURL.REGISTER;
import static pl.crewops.enums.ControllerURL.VERIFY_EMAIL;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.crewops.model.dto.registration.CreateCustomerCommand;
import pl.crewops.model.dto.registration.CreateCustomerResult;
import pl.crewops.model.dto.registration.PreRegisterResponse;
import pl.crewops.model.dto.registration.VerifyEmailRequest;

@RestController
@RequiredArgsConstructor
@Validated
class RegistrationController {

    private final RegistrationService registrationService;

    @PostMapping(REGISTER)
    public ResponseEntity<PreRegisterResponse> registerCustomer(
            @NotNull @Valid @RequestBody CreateCustomerCommand createCustomerCommand) {
        return ResponseEntity.ok(registrationService.registerCustomer(createCustomerCommand));
    }

    @PostMapping(VERIFY_EMAIL)
    public ResponseEntity<CreateCustomerResult> verifyEmail(
            @NotNull @Valid @RequestBody VerifyEmailRequest verifyEmailRequest) {
        return ResponseEntity.ok(registrationService.finalizeRegisterCustomer(verifyEmailRequest));
    }
}
