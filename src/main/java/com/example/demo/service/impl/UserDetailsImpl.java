package com.example.demo.service.impl;

import com.example.demo.entity.OAuth2UserEntity;
import com.example.demo.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;


@Data
@AllArgsConstructor
public class UserDetailsImpl implements UserDetails {
    private User user;
    private OAuth2UserEntity oAuth2User;


    public static UserDetailsImpl builder(User user) {
        return new UserDetailsImpl(
            user,
            user.getOauth2User()
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority("USER"));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    public String getEmail() {
        return user.getEmail();
    }

    public Map<String, Object> toAttributeMap() {
        return Map.of(
                "id", this.user.getId(),
                "username", this.getUsername(),
                "email", this.getEmail(),
                "authorities", this.getAuthorities()
        );
    }

    public Map<String, Object> getAttributeMapOAuth2() {
        return Map.of(
                "id", this.user.getId(),
                "sub", this.oAuth2User.getSub(),
                "username", this.getUsername(),
                "email", this.getEmail(),
                "registrationId", this.oAuth2User.getProvider(),
                "picture", this.user.getAvatar(),
                "authorities", this.getAuthorities()
        );
    }
}
