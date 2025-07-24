package pl.crewops.domain.auth;

import static pl.crewops.enums.ControllerURL.*;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.crewops.auth.*;
import pl.crewops.dto.employee.CreateEmployeeDTO;
import pl.crewops.security.custom.permissionAnnotation.ManagerPermission;

@RestController
@RequiredArgsConstructor
class AuthController {
    private final AuthAPI authAPI;

    @PostMapping(LOGIN)
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody AuthRequest authRequest, HttpServletResponse response) {
        return ResponseEntity.status(HttpStatus.OK).body(authAPI.login(authRequest, response));
    }

    @PostMapping(VALIDATE)
    public ResponseEntity<ValidTokenResponse> validate(@Valid @RequestBody ValidTokenRequest validTokenRequest) {
        return ResponseEntity.status(HttpStatus.OK).body(authAPI.validateToken(validTokenRequest));
    }

    @PostMapping(EMPLOYEES)
    @ManagerPermission
    public ResponseEntity<CreateAuthUserResult> createAuthUserWithRelatedEmployee(
            @NotNull @Valid @RequestBody CreateEmployeeDTO createEmployeeDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authAPI.createAuthUserWithRelatedEmployee(createEmployeeDTO));
    }

    @DeleteMapping(EMPLOYEES_EID)
    public ResponseEntity<Void> deleteEmployee(@PathVariable(EMPLOYEE_ID) UUID employeeId) {
        authAPI.terminateEmployeeAuthUserAccount(employeeId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
