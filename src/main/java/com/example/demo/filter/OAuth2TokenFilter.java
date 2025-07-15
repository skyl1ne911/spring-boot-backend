package com.example.demo.filter;

import com.example.demo.controller.SecurityController;
import com.example.demo.exception.CookieException;
import com.example.demo.mapper.AuthTokenStorage;
import com.example.demo.mapper.TokenValidationResult;
import com.example.demo.configuration.CookieConfigure;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import com.example.demo.service.impl.JwtService;
import com.example.demo.service.impl.UserDetailsImpl;
import com.example.demo.service.impl.UserDetailsServiceImpl;
import com.example.demo.service.impl.UserServiceImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.ClientAuthorizationRequiredException;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Objects;

@Slf4j
@Component
public class OAuth2TokenFilter extends OncePerRequestFilter {
    private static final RequestMatcher SKIP_OAUTH2 = new AntPathRequestMatcher(OAuth2LoginAuthenticationFilter.DEFAULT_FILTER_PROCESSES_URI);

    private final JwtService jwtService;
    private final ApplicationContext applicationContext;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;

    @Autowired
    public OAuth2TokenFilter(JwtService jwtService, ApplicationContext applicationContext) {
        this.jwtService = jwtService;
        this.applicationContext = applicationContext;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return SKIP_OAUTH2.matches(request);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            Claims claims = jwtService.extractClaimsAndTokens(request, response);
            jwtFromCookie(claims);
            filterChain.doFilter(request, response);
        } catch (CookieException ex) {
            log.error("Cookie exception: {}", ex.getMessage());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized: Missing cookies which contains token");
        } catch (JwtException ex) {
            log.error("JWT exception during authentication from cookie: {}", ex.getMessage());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized: Invalid or missing token");
        } catch (ClientAuthorizationRequiredException ex) {
            log.error("Client authorization exception: {}", ex.getMessage());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized: Client auth required");
        } catch (Exception ex) {
            log.error("JWT authentication failed: {}", ex.getMessage());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized: Authentication failed");
        }



        log.info(request.getRequestURI());
    }

    private void jwtFromCookie(Claims claims) throws JwtException, ClientAuthorizationRequiredException {
        String email = claims.getSubject();
        String authType = claims.get(JwtService.CLAIM_KEY_AUTH_TYPE).toString();

        if (Objects.equals(authType, JwtService.AUTH_OAUTH2) && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetailsImpl userDetails = (UserDetailsImpl) applicationContext.getBean(UserDetailsServiceImpl.class).loadUserByUsername(email);
            String registrationId = claims.get(JwtService.CLAIM_KEY_REGISTRATION_ID).toString();

            Authentication authentication = createAuthentication(registrationId, userDetails);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            log.info("User successfully authorization and tokens saved in cookie");
        }
        else {
            log.warn("authentication must be null or authentication type doesn't equal '{}'", JwtService.AUTH_OAUTH2);
        }
    }

    private Authentication createAuthentication(String registrationId, UserDetailsImpl userDetails) {
        if (registrationId != null) {
            OAuth2User oAuth2User = new DefaultOAuth2User(
                    userDetails.getAuthorities(),
                    userDetails.getAttributeMapOAuth2(),
                    "username"
            );
            return new OAuth2AuthenticationToken(oAuth2User, oAuth2User.getAuthorities(), registrationId);
        }
        log.error("Registration id is null");
        throw new ClientAuthorizationRequiredException("Client has not been registered");
    }
}
