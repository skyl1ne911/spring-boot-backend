package com.example.demo.security.oauth2;

import com.example.demo.configuration.CookieConfigure;
import com.example.demo.configuration.SecurityConfig;
import com.example.demo.entity.OAuth2UserEntity;
import com.example.demo.entity.User;
import com.example.demo.mapper.AuthTokenStorage;
import com.example.demo.repository.OAuth2UserRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.cloudinary.CloudinaryService;
import com.example.demo.service.impl.JwtService;
import com.example.demo.service.impl.UserServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.util.Objects;

@Slf4j
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;

    @Autowired
    private OAuth2UserRepository o2UserRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private CloudinaryService cloudinaryService;

    @SneakyThrows
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
        OAuth2User principal = token.getPrincipal();

        String username =  principal.getAttribute(UserServiceImpl.OAUTH_ATTR_USERNAME);
        String email = principal.getAttribute(UserServiceImpl.OAUTH_ATTR_EMAIL);
        String registrationId = token.getAuthorizedClientRegistrationId();

        if (!userRepository.existsByEmail(email)) {
            OAuth2UserEntity oAuth2User = new OAuth2UserEntity(principal.getAttribute("sub"), registrationId);
            String uploadResponse = cloudinaryService.uploadImage(Objects.requireNonNull(principal.getAttribute("picture")));
            User user = new User(username, email);
            user.setAvatar(uploadResponse);
            user.setOauth2User(oAuth2User);
            userRepository.save(user);
        }

        AuthTokenStorage tokens = jwtService.generateOAuth2Tokens(username, email, registrationId);

        String accessToken = CookieConfigure.generateCookie(CookieConfigure.COOKIE_OAUTH_ACCESS_TOKEN, tokens.accessToken(), CookieConfigure.COOKIE_EXPIRE_ACCESS_TOKEN);
        String refreshTokenCookie = CookieConfigure.generateCookie(CookieConfigure.COOKIE_OAUTH_REFRESH_TOKEN, tokens.refreshToken(), CookieConfigure.COOKIE_EXPIRE_REFRESH_TOKEN);

        response.addHeader(HttpHeaders.SET_COOKIE, accessToken);
        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie);
        response.sendRedirect(SecurityConfig.URI_FRONTEND_CORS);

        log.info("Method: onAuthenticationSuccess; tokens - {}", tokens);
    }

}
