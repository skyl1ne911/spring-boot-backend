package com.example.demo.security.oauth2;

import com.example.demo.configuration.CookieConfigure;
import com.example.demo.security.CryptConfig;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.http.auth.AUTH;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

import java.time.Duration;
import java.util.UUID;


public class OAuth2RequestRepositoryStateless implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {
    private static final Logger LOG = LoggerFactory.getLogger(OAuth2RequestRepositoryStateless.class);
    public static final String REDIS_KEY_AUTHORIZATION_REQUEST = "authorization_request:";

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
        String key = getKey(request);
        LOG.info("loadAuthorizationRequest: load 'authorizationRequest' from redis with key={}", key);
        return (OAuth2AuthorizationRequest) redisTemplate.opsForValue().get(key);
    }

    @Override
    public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest, HttpServletRequest request, HttpServletResponse response) {
        if (authorizationRequest == null) {
            this.removeAuthorizationRequest(request, response);
            LOG.warn("saveAuthorizationRequest: 'authorizationRequest' is null;");
            return;
        }

        String key = generateKey(response);
        redisTemplate.opsForValue().set(key, authorizationRequest, Duration.ofMinutes(3));

        LOG.info("'authorizationRequest' saved to redis with key={}", key);
    }

    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request, HttpServletResponse response) {
        OAuth2AuthorizationRequest auth2AuthorizationRequest = loadAuthorizationRequest(request);
        String key = getKey(request);
        redisTemplate.delete(key);

        String expiredOAuth2Request = CookieConfigure.generateExpiredCookie(CookieConfigure.COOKIE_REQUEST_OAUTH2);
        response.addHeader(HttpHeaders.SET_COOKIE, expiredOAuth2Request);

        LOG.info("removeAuthorizationRequest: delete 'authorizationRequest' from redis with key={}", key);
        return auth2AuthorizationRequest;
    }

    private String getKey(HttpServletRequest request) {
        return REDIS_KEY_AUTHORIZATION_REQUEST +
                CookieConfigure.getCookie(request.getCookies(), CookieConfigure.COOKIE_REQUEST_OAUTH2)
                .orElse(null);
    }

    private String generateKey(HttpServletResponse response) {
        String uuid = UUID.randomUUID().toString();
        String cookie = CookieConfigure.generateCookie(CookieConfigure.COOKIE_REQUEST_OAUTH2, uuid, Duration.ofMinutes(3));
        response.addHeader(HttpHeaders.SET_COOKIE, cookie);
        return REDIS_KEY_AUTHORIZATION_REQUEST + uuid;
    }
}
