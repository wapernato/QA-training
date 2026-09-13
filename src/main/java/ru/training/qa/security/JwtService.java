package ru.training.qa.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.training.qa.exception.UnauthorizedException;
import ru.training.qa.user.User;

@Service
public class JwtService {
    private final Algorithm algorithm;
    private final Duration expiration;
    public JwtService(@Value("${app.jwt.secret}") String secret, @Value("${app.jwt.expiration-minutes}") long minutes) {
        algorithm = Algorithm.HMAC256(secret);
        expiration = Duration.ofMinutes(minutes);
    }
    public String issue(User user) {
        Instant now = Instant.now();
        return JWT.create().withSubject(user.getId().toString()).withClaim("username", user.getUsername())
                .withIssuedAt(Date.from(now)).withExpiresAt(Date.from(now.plus(expiration))).sign(algorithm);
    }
    public long userId(String token) {
        try { return Long.parseLong(JWT.require(algorithm).build().verify(token).getSubject()); }
        catch (JWTVerificationException | NumberFormatException exception) { throw new UnauthorizedException("Token is invalid or expired"); }
    }
    public long expiresInSeconds() { return expiration.toSeconds(); }
}
