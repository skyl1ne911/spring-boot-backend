package com.example.demo.configuration;


import jakarta.servlet.http.Cookie;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.SameSiteCookies;
import org.springframework.http.ResponseCookie;

import java.time.Duration;
import java.util.Arrays;
import java.util.Optional;

@Slf4j
public class CookieConfigure {
    public static final String COOKIE_OAUTH_ACCESS_TOKEN = "ACCESS-TOKEN";
    public static final String COOKIE_OAUTH_REFRESH_TOKEN = "REFRESH-TOKEN";
    public static final String COOKIE_REQUEST_OAUTH2 = "OAUTH";
    public static final Duration COOKIE_EXPIRE_ACCESS_TOKEN = Duration.ofMinutes(20);
    public static final Duration COOKIE_EXPIRE_REFRESH_TOKEN = Duration.ofHours(2);

    private static final String DOMAIN = "localhost";
    private static final Boolean HTTP_ONLY = Boolean.TRUE;
    private static final Boolean SECURE = Boolean.TRUE;
    public static final Duration LIFE_TIME = Duration.ofMinutes(60);

    public static Optional<String> getCookie(Cookie[] cookies, String name) {
        if (cookies == null) return Optional.empty();

        return Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equalsIgnoreCase(name))
                .map(Cookie::getValue)
                .findFirst();
    }

    public static String generateCookie(String name, String value, Duration lifeTime) {
        ResponseCookie responseCookie = ResponseCookie.from(name, value)
                .httpOnly(HTTP_ONLY)
                .secure(SECURE)
                .path("/")
                .maxAge((int) lifeTime.getSeconds())
                .sameSite(SameSiteCookies.NONE.getValue())
                .build();

        log.info("cookie created");
        return responseCookie.toString();
    }

    public static String generateExpiredCookie(String name) {
        return generateCookie(name, "-", Duration.ZERO);
    }

}
