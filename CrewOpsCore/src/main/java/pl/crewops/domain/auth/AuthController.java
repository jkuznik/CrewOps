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
import pl.crewops.auth.AuthRequest;
import pl.crewops.auth.AuthResponse;
import pl.crewops.auth.ValidTokenRequest;
import pl.crewops.auth.ValidTokenResponse;
import pl.crewops.dto.employee.CreateEmployeeDTO;
import pl.crewops.dto.employee.EmployeeDTO;

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
    public ResponseEntity<EmployeeDTO> createEmployee(
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
