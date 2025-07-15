package com.example.demo.controller;

import com.example.demo.dto.AuthResponseDto;
import com.example.demo.security.logout.CustomLogoutSuccessHandler;
import com.example.demo.security.oauth2.CustomAuthorizedClientService;
import com.example.demo.service.cloudinary.CloudinaryService;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;

import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.Map;


@RestController
@RequestMapping("/sec")
@Tag(name = "Security Controller", description = "Test Security API")
public class SecurityController {
    public static final String URI_SECURED = "/sec";

    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;


    @Autowired
    private ClientRegistrationRepository clientRegistrationRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private CustomAuthorizedClientService customAuthorizedClientService;

    @Autowired
    private CloudinaryService cloudinaryService;

    @GetMapping
    public String welcome() {
        return "Hello";
    }

    @GetMapping("/postman")
    public Principal postman(Principal principal) {
        return principal;
    }

    @GetMapping("/work")
    public String workPublic() {
        return "work";
    }

    @GetMapping("/remove")
    public String removeClient() {
//        ClientRegistration registration =  clientRegistrationRepository.findByRegistrationId("google");
//        OAuth2AuthorizedClient client = redisService.findByKey(CustomAuthorizedClientService.REDIS_KEY_AUTH_CLIENT, customAuthorizedClientService.getHashKey());
        authorizedClientService.removeAuthorizedClient("", "");
        return "removed";
    }

    @GetMapping("/load")
    public OAuth2AuthorizedClient loadClient() {
        OAuth2AuthenticationToken authentication = (OAuth2AuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        return authorizedClientService.loadAuthorizedClient(
                authentication.getAuthorizedClientRegistrationId(),
                authentication.getPrincipal().getAttribute("email")
        );
    }

    @GetMapping("/get-auth")
    public AuthResponseDto getAuth(Authentication authentication) throws Exception {
        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
        OAuth2User principal = token.getPrincipal();
        String avatar = cloudinaryService.loadImage(principal.getAttribute("picture"));
        return AuthResponseDto.builder()
                .id(principal.getAttribute("sub"))
                .username(principal.getName())
                .email(principal.getAttribute("email"))
                .picture(avatar)
                .response("successfully authorized")
                .build();
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body("You have logged out");
    }

}
