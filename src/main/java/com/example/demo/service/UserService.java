package com.example.demo.service;

import com.example.demo.dto.SigninRequestDto;
import com.example.demo.dto.SignupRequestDto;
import com.example.demo.entity.User;
import com.example.demo.exception.UnauthorizedException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

import java.io.IOException;


public interface UserService {

    Authentication signup(SignupRequestDto signupRequestDto, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;

    String signin(SigninRequestDto signinRequestDto, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException;

    String authentication(SigninRequestDto signinRequestDto) throws UnauthorizedException;

    User saveAuthenticationUser(OAuth2AuthenticationToken oauth2Token);

}
