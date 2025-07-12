package com.example.demo.exception;

import com.example.demo.exception.data.UnauthorizedExceptionData;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<UnauthorizedExceptionData> unauthorizedExceptionHandler(UnauthorizedException ex) {
        UnauthorizedExceptionData exClass = new UnauthorizedExceptionData(ex.getMessage(), HttpStatus.UNAUTHORIZED.value());

        if (ex.getEmail() != null) exClass.setEmail(ex.getEmail());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(exClass);
    }

    @ExceptionHandler(PasswordValidationException.class)
    public ResponseEntity<String> passwordValidationException(PasswordValidationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(EmailNotFoundException.class)
    public ResponseEntity<String> emailExceptions(EmailNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

}
