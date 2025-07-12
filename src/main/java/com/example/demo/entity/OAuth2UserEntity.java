package com.example.demo.entity;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.oauth2.core.user.OAuth2User;


@Entity(name = "oauth2users")
@NoArgsConstructor
@Data
public class OAuth2UserEntity {

    @Id
    private String sub;

    @Column
    private String provider;


    public OAuth2UserEntity(String sub, String registrationId) {
        this.sub = sub;
        this.provider = registrationId;
    }

}
