package ru.training.qa.user;

import org.springframework.context.annotation.Bean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.training.qa.api.*;
import ru.training.qa.exception.ConflictException;
import ru.training.qa.exception.UnauthorizedException;
import ru.training.qa.security.JwtService;

@Service
public class AuthService {
    private final UserRepository users; private final PasswordEncoder encoder; private final JwtService jwt;
    public AuthService(UserRepository users, PasswordEncoder encoder, JwtService jwt) { this.users = users; this.encoder = encoder; this.jwt = jwt; }
    @Transactional public UserResponse register(RegisterRequest request) {
        try {
            User user = users.saveAndFlush(new User(request.username(), encoder.encode(request.password())));
            return new UserResponse(user.getId(), user.getUsername());
        } catch (DataIntegrityViolationException exception) { throw new ConflictException("Username already exists"); }
    }
    @Transactional(readOnly = true) public TokenResponse login(LoginRequest request) {
        User user = users.findByUsername(request.username()).filter(u -> encoder.matches(request.password(), u.getPasswordHash()))
                .orElseThrow(() -> new UnauthorizedException("Invalid username or password"));
        return new TokenResponse(jwt.issue(user), "Bearer", jwt.expiresInSeconds());
    }
    @Bean public static PasswordEncoder passwordEncoder() { return new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder(); }
}
