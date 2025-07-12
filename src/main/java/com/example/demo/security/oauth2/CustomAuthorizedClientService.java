package com.example.demo.security.oauth2;

import com.example.demo.model.RedisKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@Order(1)
public class CustomAuthorizedClientService implements OAuth2AuthorizedClientService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public <T extends OAuth2AuthorizedClient> T loadAuthorizedClient(String clientRegistrationId, String email) {
        log.info("loadAuthorizedClient: load authorizedClient from redis");

        String hashKey = RedisKey.authorizationClientHashKey(clientRegistrationId, email);
        return (T) redisTemplate.opsForHash().get(RedisKey.REDIS_KEY_AUTH_CLIENT, hashKey);
    }

    @Override
    public void saveAuthorizedClient(OAuth2AuthorizedClient authorizedClient, Authentication principal) {
        OAuth2User user = (OAuth2User) principal.getPrincipal();
        String hashKey = RedisKey.authorizationClientHashKey(authorizedClient.getClientRegistration().getRegistrationId(), user.getAttribute("email"));

        redisTemplate.opsForHash().put(RedisKey.REDIS_KEY_AUTH_CLIENT, hashKey, authorizedClient);
        log.info("saveAuthorizedClient: saved authorizedClient to redis");
    }

    @Override
    public void removeAuthorizedClient(String clientRegistrationId, String email) {
        String hashKey = RedisKey.authorizationClientHashKey(clientRegistrationId, email);

        redisTemplate.opsForHash().delete(RedisKey.REDIS_KEY_AUTH_CLIENT,  hashKey);
        log.info("removeAuthorizedClient: remove authorizedClient from redis");
    }
}