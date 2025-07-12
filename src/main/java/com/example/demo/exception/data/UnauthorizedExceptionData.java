package com.example.demo.exception.data;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UnauthorizedExceptionData {
    private String message;
    private String email;
    private int status;

    public UnauthorizedExceptionData(String message, int status) {
        this.message = message;
        this.status = status;
    }

}
