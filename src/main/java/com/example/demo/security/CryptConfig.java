package com.example.demo.security;

import org.apache.commons.lang3.SerializationUtils;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

import java.util.Base64;

public class CryptConfig {

    public static String encrypt(OAuth2AuthorizationRequest oauth2Request) {
        byte[] bytes = SerializationUtils.serialize(oauth2Request);
        return Base64.getEncoder().encodeToString(bytes);
    }

    public static OAuth2AuthorizationRequest decrypt(String encrypt) {
        byte[] bytes = Base64.getDecoder().decode(encrypt);
        return SerializationUtils.deserialize(bytes);
    }

}
