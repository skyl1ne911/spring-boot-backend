package com.example.demo.model;

public class RedisKey {
    public static String REDIS_KEY_AUTH_CLIENT = "oauth2client";
    public static String REDIS_KEY_ACCESS_TOKEN = "accessToken";

    public static String authorizationClientHashKey(String clientRegistrationId, String email) {
        return String.format("%s:%s",
                clientRegistrationId,
                email
        );
    }
}
