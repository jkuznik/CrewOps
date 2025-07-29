package pl.crewops.domain.registration;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.crewops.enums.ControllerURL;
import pl.crewops.registration.CreateCustomerCommand;
import pl.crewops.registration.CreateCustomerResult;
import pl.crewops.security.custom.permissionAnnotation.SystemAdminPermission;

@RestController
@SystemAdminPermission
@RequiredArgsConstructor
@Validated
class RegistrationController {

    private final RegistrationService registrationService;

    @PostMapping(ControllerURL.REGISTER)
    public ResponseEntity<CreateCustomerResult> registerCustomer(
            @NotNull @Valid @RequestBody CreateCustomerCommand createCustomerCommand) {
        return ResponseEntity.ok(registrationService.registerCustomer(createCustomerCommand));
    }
}
