package com.example.demo.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@JsonIgnoreProperties()
@AllArgsConstructor
public class SigninRequestDto {
    private String email;
    private String password;
}
