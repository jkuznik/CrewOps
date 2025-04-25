package pl.crewops.domain.auth;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.crewops.auth.AuthRequest;
import pl.crewops.auth.AuthResponse;
import pl.crewops.enums.ControllerURL;

@RestController
@Slf4j
@RequiredArgsConstructor
class AuthController {
    private final AuthService authService;

    @PostMapping(ControllerURL.LOGIN)
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody AuthRequest authRequest, HttpServletResponse response) {
        return ResponseEntity.status(HttpStatus.OK).body(authService.login(authRequest, response));
    }
}
