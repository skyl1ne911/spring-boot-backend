package com.example.demo.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SignupRequestDto {
    private String username;
    private String email;
    private String password;
}
