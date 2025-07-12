package com.example.demo.security.oauth2;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

public final class OAuth2Handler {

    @SneakyThrows
    public static void oauthSuccessHandler(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        response.addCookie(new Cookie("SESSION", "success"));
        response.getWriter().write("Thanks for login");
    }

    @SneakyThrows
    public static void oauthFailureHandler(HttpServletRequest request, HttpServletResponse response, AuthenticationException authenticationException) {
        response.getWriter().write("Failed login");
    }
}
