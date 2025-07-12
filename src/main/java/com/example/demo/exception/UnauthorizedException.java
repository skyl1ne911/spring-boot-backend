package com.example.demo.exception;


import lombok.Data;

import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@EqualsAndHashCode(callSuper = true)
@Data
@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class UnauthorizedException extends RuntimeException {
    private String email;

    public UnauthorizedException() {
        super();
    }

    public UnauthorizedException(String message) {
        super(message);
    }

    public UnauthorizedException setEmail(String email) {
        this.email = email;
        return this;
    }

}
