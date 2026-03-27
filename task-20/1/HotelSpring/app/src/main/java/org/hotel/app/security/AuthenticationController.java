package org.hotel.app.security;

import org.hotel.model.enums.Role;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/hotel/auth")
public class AuthenticationController {

    /**
     * Сервис аутентификации.
     */
    private final AuthenticationService authenticationService;

    public AuthenticationController(final AuthenticationService authenticationServiceP) {
        this.authenticationService = authenticationServiceP;
    }

    public record RegisterRequest(String username, String password, Role role) {
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        String token = authenticationService.register(request.username(), request.password(), request.role());
        return ResponseEntity.ok(Map.of("token", token));
    }

    public record AuthRequest(String username, String password) {
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticate(@RequestBody AuthRequest request) {
        String token = authenticationService.authenticate(request.username(), request.password());
        return ResponseEntity.ok(Map.of("token", token));
    }
}
