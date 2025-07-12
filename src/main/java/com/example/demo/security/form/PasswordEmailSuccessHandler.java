package com.example.demo.security.form;

import com.example.demo.configuration.CookieConfigure;
import com.example.demo.mapper.AuthTokenStorage;
import com.example.demo.service.impl.JwtService;
import com.example.demo.service.impl.UserDetailsImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;

@Component
@Slf4j
public class PasswordEmailSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private JwtService jwtService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) authentication;
        UserDetailsImpl userDetails = (UserDetailsImpl) token.getPrincipal();

        AuthTokenStorage tokens = jwtService.generatePasswordLoginTokens(userDetails.getUsername(), userDetails.getEmail());

        String cookieAccessJwt = CookieConfigure.generateCookie(CookieConfigure.COOKIE_OAUTH_ACCESS_TOKEN, tokens.accessToken(), CookieConfigure.COOKIE_EXPIRE_ACCESS_TOKEN);
        String cookieRefreshJwt = CookieConfigure.generateCookie(CookieConfigure.COOKIE_OAUTH_REFRESH_TOKEN, tokens.refreshToken(), CookieConfigure.COOKIE_EXPIRE_REFRESH_TOKEN);

        response.addHeader(HttpHeaders.SET_COOKIE, cookieAccessJwt);
        response.addHeader(HttpHeaders.SET_COOKIE, cookieRefreshJwt);

        log.info("invoked method - onAuthenticationSuccess;");
    }
}
