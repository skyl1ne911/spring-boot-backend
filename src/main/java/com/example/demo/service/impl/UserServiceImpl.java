package com.example.demo.service.impl;


import com.example.demo.dto.SigninRequestDto;
import com.example.demo.dto.SignupRequestDto;
import com.example.demo.entity.User;
import com.example.demo.exception.EmailNotFoundException;
import com.example.demo.exception.UnauthorizedException;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.form.PasswordEmailSuccessHandler;
import com.example.demo.service.UserService;
import com.example.demo.validation.impl.PasswordValidation;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    public static final String OAUTH_ATTR_USERNAME = "given_name";
    public static final String OAUTH_ATTR_EMAIL = "email";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordValidation passwordValidation;

    @Autowired
    private PasswordEmailSuccessHandler successHandler;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Override
    public Authentication signup(SignupRequestDto signupRequestDto, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (userRepository.existsByEmail(signupRequestDto.getEmail())) throw new EmailNotFoundException("User already registered");
        passwordValidation.validation(signupRequestDto.getPassword());

        User user = new User(signupRequestDto.getUsername(), signupRequestDto.getEmail());
        user.setNoCryptPassword(signupRequestDto.getPassword());
        user.setPassword(passwordEncoder.encode(signupRequestDto.getPassword()));
        userRepository.save(user);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        signupRequestDto.getEmail(),
                        signupRequestDto.getPassword()
                )
        );

        successHandler.onAuthenticationSuccess(request, response, authentication);

        log.info("User {} with email successfully registration", signupRequestDto.getUsername());
        return authentication;
    }

    @Override
    public String signin(SigninRequestDto signinRequestDto, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        if (!userRepository.existsByEmail(signinRequestDto.getEmail())) throw new EmailNotFoundException("User doesn't exists");
        passwordValidation.validation(signinRequestDto.getPassword());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        signinRequestDto.getEmail(),
                        signinRequestDto.getPassword()
                )
        );

        successHandler.onAuthenticationSuccess(request, response, authentication);

        return authentication.getName();
    }

    @Override
    public String authentication(SigninRequestDto signinRequestDto) throws UnauthorizedException {
        return "";
    }

    @Override
    public User saveAuthenticationUser(OAuth2AuthenticationToken oauth2Token) {
        OAuth2User oauth2User = oauth2Token.getPrincipal();

        String username = oauth2User.getAttribute(OAUTH_ATTR_USERNAME);
        String email = oauth2User.getAttribute(OAUTH_ATTR_EMAIL);
        String provider = oauth2Token.getAuthorizedClientRegistrationId();


        if (!userRepository.existsByEmail(email)) {
            log.info("User successfully created");
            return userRepository.save(new User(username, email));
        }

        log.info("User Already exists");
        return null;
    }
}
