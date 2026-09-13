package ru.training.qa.api;

import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.training.qa.user.AuthService;

@RestController @RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService auth;
    public AuthController(AuthService auth) { this.auth = auth; }
    @PostMapping("/register") public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        UserResponse user = auth.register(request);
        return ResponseEntity.created(URI.create("/api/v1/users/" + user.id())).body(user);
    }
    @PostMapping("/login") public TokenResponse login(@Valid @RequestBody LoginRequest request) { return auth.login(request); }
}
