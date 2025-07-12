package com.example.demo.validation;

import com.example.demo.dto.SignupRequestDto;

import java.util.Map;

public interface SignupRequestValid {
    Map<String, String> validSignupRequest(SignupRequestDto signupRequestDto);
}
