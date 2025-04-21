package pl.crewops.domain.auth;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.crewops.auth.AuthRequest;
import pl.crewops.auth.AuthResponse;
import pl.crewops.enums.ControllerURL;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping(ControllerURL.LOGIN)
    public ResponseEntity<AuthResponse> login(
            //            @Valid @RequestBody AuthRequest authRequest, HttpServletResponse response) {
            @Valid @RequestParam String username, @Valid @RequestParam String password, HttpServletResponse response) {
        var authRequest = new AuthRequest(username, password);
        return ResponseEntity.status(HttpStatus.OK).body(authService.login(authRequest, response));
    }
}
