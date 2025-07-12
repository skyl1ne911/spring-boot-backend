package com.example.demo.validation.impl;

import com.example.demo.dto.SignupRequestDto;
import com.example.demo.repository.UserRepository;

import com.example.demo.validation.SignupRequestValid;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class SignupRequestValidImpl implements SignupRequestValid {

    private final UserRepository userRepository;

    @Autowired
    public SignupRequestValidImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public Map<String, String> validSignupRequest(SignupRequestDto signupRequestDto) {
        Map<String, String> errors = new HashMap<>();

        if (userRepository.existsByEmail(signupRequestDto.getEmail())){
            errors.put("email", "User already registered");
        }
        else if (signupRequestDto.getEmail().isBlank()) {
            errors.put("email", "Email is empty");
        }

        if (signupRequestDto.getUsername().isBlank()) {
            errors.put("username", "Username is empty");
        }

        return errors;
    }
}
