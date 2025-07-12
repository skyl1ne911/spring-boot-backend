package com.example.demo.security.logout;

import com.example.demo.configuration.CookieConfigure;
import com.example.demo.controller.UserController;
import com.example.demo.exception.CookieException;
import com.example.demo.mapper.AuthTokenStorage;
import com.example.demo.service.impl.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.swing.text.html.Option;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;

@Slf4j
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {
    private OidcClientInitiatedLogoutSuccessHandler delegate;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;

    @Autowired
    private ClientRegistrationRepository clientRegistrationRepository;

    public CustomLogoutSuccessHandler(ClientRegistrationRepository clientRegistrationRepository) {
        delegate = new OidcClientInitiatedLogoutSuccessHandler(clientRegistrationRepository);
    }

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        logoutOAuth2(request, response);

        String accessToken = CookieConfigure.generateExpiredCookie(CookieConfigure.COOKIE_OAUTH_ACCESS_TOKEN);
        String refreshToken = CookieConfigure.generateExpiredCookie(CookieConfigure.COOKIE_OAUTH_REFRESH_TOKEN);

        response.addHeader(HttpHeaders.SET_COOKIE, accessToken);
        response.addHeader(HttpHeaders.SET_COOKIE, refreshToken);

        response.sendRedirect(UserController.HOME_PAGE);
        delegate.onLogoutSuccess(request, response, authentication);

        log.info("onLogoutSuccess: user logged");
    }

    private void logoutOAuth2(HttpServletRequest request, HttpServletResponse response) {
        Claims claims = jwtService.extractClaimsAndTokens(request, response);
        String authType = claims.get(JwtService.CLAIM_KEY_AUTH_TYPE).toString();

        if (Objects.equals(authType, JwtService.AUTH_OAUTH2)) {
            String email = claims.getSubject();
            String registrationId = claims.get(JwtService.CLAIM_KEY_REGISTRATION_ID).toString();
            authorizedClientService.removeAuthorizedClient(registrationId, email);
        }

    }

}
