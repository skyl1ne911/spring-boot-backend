package com.example.demo.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.oauth2.core.user.OAuth2User;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDto {
    private String id;
    private String username;
    private String email;
    private String picture;
    private String response;
}
