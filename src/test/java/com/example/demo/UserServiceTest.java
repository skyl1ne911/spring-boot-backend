package com.example.demo;

import com.example.demo.dto.SigninRequestDto;
import com.example.demo.dto.SignupRequestDto;
import com.example.demo.entity.User;
import com.example.demo.exception.EmailNotFoundException;
import com.example.demo.exception.PasswordValidationException;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.form.PasswordEmailSuccessHandler;
import com.example.demo.service.impl.UserServiceImpl;
import com.example.demo.validation.impl.PasswordValidation;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private PasswordValidation passwordValidation;

    @Mock
    private PasswordEmailSuccessHandler successHandler;

    private SigninRequestDto signinRequestDto;
    private SignupRequestDto signupRequestDto;

    private HttpServletRequest request;
    private HttpServletResponse response;

    @BeforeEach
    void setUp() {
        signinRequestDto = new SigninRequestDto("john@test", "2890371284eqw");
        signupRequestDto = new SignupRequestDto("john", "john@test", "2890371284eqw");
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
    }


    @Test
    void signup_validData_returnAuthentication() throws ServletException, IOException {
        when(userRepository.existsByEmail(signinRequestDto.getEmail())).thenReturn(false);
        doNothing().when(passwordValidation).validation(signinRequestDto.getPassword());
        when(passwordEncoder.encode(signinRequestDto.getPassword())).thenReturn("encodedPassword");

        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);

        Authentication result = userService.signup(signupRequestDto, request, response);

        assertNotNull(result);
        verify(userRepository).existsByEmail(signupRequestDto.getEmail());

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertEquals(signupRequestDto.getUsername(), savedUser.getUsername());
        assertEquals(signupRequestDto.getEmail(), savedUser.getEmail());
        assertEquals("encodedPassword", savedUser.getPassword());
        assertEquals(signupRequestDto.getPassword(), savedUser.getNoCryptPassword());

        verify(passwordValidation).validation(signupRequestDto.getPassword());
        verify(passwordEncoder).encode(signupRequestDto.getPassword());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(successHandler).onAuthenticationSuccess(request, response, authentication);
    }

    @Test
    void signin_validEmailPassword_returnAuthorizationName() throws ServletException, IOException {
        Authentication authentication = mock(Authentication.class);

        when(userRepository.existsByEmail(signinRequestDto.getEmail())).thenReturn(true);
        doNothing().when(passwordValidation).validation(signinRequestDto.getPassword());
        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(authentication);
        when(authentication.getName()).thenReturn("john@test");

        String authName = userService.signin(signinRequestDto, request, response);

        assertEquals(signinRequestDto.getEmail(), authName);
        verify(userRepository).existsByEmail(signinRequestDto.getEmail());
        verify(authenticationManager).authenticate(any(Authentication.class));
        verify(successHandler).onAuthenticationSuccess(request, response, authentication);
    }

    @Test
    void signin_invalidEmail_throwsException() throws ServletException, IOException {
        when(userRepository.existsByEmail(signinRequestDto.getEmail())).thenReturn(false);
        assertThrows(EmailNotFoundException.class, () -> userService.signin(signinRequestDto, request, response));

        verify(userRepository).existsByEmail(any(String.class));
        verifyNoInteractions(authenticationManager);
        verifyNoInteractions(successHandler);
    }

    @Test
    void signin_invalidPassword_throwsException() {
        when(userRepository.existsByEmail(signinRequestDto.getEmail())).thenReturn(true);
        doThrow(new PasswordValidationException("Invalid password")).when(passwordValidation).validation(signinRequestDto.getPassword());

        assertThrows(PasswordValidationException.class, () -> userService.signin(signinRequestDto, request, response));

        verify(userRepository).existsByEmail(any(String.class));
        verifyNoInteractions(authenticationManager);
        verifyNoInteractions(successHandler);
    }
}
