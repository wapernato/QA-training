package ru.training.qa.api;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.training.qa.exception.ConflictException;
import ru.training.qa.exception.NotFoundException;
import ru.training.qa.exception.UnauthorizedException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(NotFoundException.class) ResponseEntity<ApiError> notFound(NotFoundException e, HttpServletRequest r) { return error(HttpStatus.NOT_FOUND, "NOT_FOUND", e.getMessage(), r); }
    @ExceptionHandler(ConflictException.class) ResponseEntity<ApiError> conflict(ConflictException e, HttpServletRequest r) { return error(HttpStatus.CONFLICT, "CONFLICT", e.getMessage(), r); }
    @ExceptionHandler(UnauthorizedException.class) ResponseEntity<ApiError> unauthorized(UnauthorizedException e, HttpServletRequest r) { return error(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", e.getMessage(), r); }
    @ExceptionHandler({MethodArgumentNotValidException.class, MethodArgumentTypeMismatchException.class})
    ResponseEntity<ApiError> badRequest(Exception e, HttpServletRequest r) { return error(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "Request is invalid", r); }
    private ResponseEntity<ApiError> error(HttpStatus status, String code, String message, HttpServletRequest r) {
        return ResponseEntity.status(status).body(new ApiError(Instant.now(), status.value(), code, message, r.getRequestURI()));
    }
}
