package com.example.demo.service.impl;


import com.example.demo.configuration.CookieConfigure;
import com.example.demo.dto.UserDto;
import com.example.demo.exception.CookieException;
import com.example.demo.exception.UnauthorizedException;
import com.example.demo.mapper.AuthTokenStorage;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.*;
import java.util.function.Function;

@Slf4j
@Component
public class JwtService {
    public final static String CLAIM_KEY_REGISTRATION_ID = "registrationId";
    public final static String CLAIM_KEY_AUTH_TYPE = "auth_type";
    public final static String AUTH_LOCAL = "local";
    public final static String AUTH_OAUTH2 = "oauth2";
    private final static long ACCESS_TOKEN_EXPIRATION = Duration.ofMinutes(10).toMillis();
    private final static long REFRESH_TOKEN_EXPIRATION = Duration.ofHours(1).toMillis();

    @Value("${security.jwt.secretkey}")
    private String hs256SecretKey;

    @Value("${security.jwt.lifetime}")
    private long LIFETIME;

    public String generateToken(UserDto userDto, String typeAuth) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", userDto.getUsername());
        claims.put(CLAIM_KEY_AUTH_TYPE, typeAuth);
        return this.generateToken(claims, userDto.getEmail(), ACCESS_TOKEN_EXPIRATION);
    }

    public AuthTokenStorage generateOAuth2Tokens(String username, String email, String registrationId) {
        Map<String, Object> claims = Map.of(
                "username", username,
                CLAIM_KEY_AUTH_TYPE, AUTH_OAUTH2,
                CLAIM_KEY_REGISTRATION_ID, registrationId
        );
        return new AuthTokenStorage(
                generateToken(claims, email, ACCESS_TOKEN_EXPIRATION),
                generateToken(claims, email, REFRESH_TOKEN_EXPIRATION)
        );
    }

    public AuthTokenStorage generatePasswordLoginTokens(String username, String email) {
        Map<String, Object> claims = Map.of("username", username, CLAIM_KEY_AUTH_TYPE, AUTH_LOCAL);

        return new AuthTokenStorage(
                generateToken(claims, email, ACCESS_TOKEN_EXPIRATION),
                generateToken(claims, email, REFRESH_TOKEN_EXPIRATION)
        );
    }

    public String extractAuthType(String token) {
        return extractClaim(token, Claims::getSubject).get();
    }

    public Claims getClaims(AuthTokenStorage tokenStorage, HttpServletResponse response) {
        Optional<Claims> accessClaims = extractAllClaims(tokenStorage.accessToken());
        if (accessClaims.isPresent()) return accessClaims.get();

        Optional<Claims> refreshClaims = extractAllClaims(tokenStorage.refreshToken());
        if (refreshClaims.isEmpty()) {
            throw new JwtException("Refresh token is invalid or expired");
        }

        Claims claims = refreshClaims.get();
        String newAccessToken = generateToken(claims, claims.getSubject(), ACCESS_TOKEN_EXPIRATION);

        String accessTokenCookie = CookieConfigure.generateCookie(CookieConfigure.COOKIE_OAUTH_ACCESS_TOKEN, newAccessToken, CookieConfigure.COOKIE_EXPIRE_ACCESS_TOKEN);
        response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie);

        return claims;
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Claims extractClaimsAndTokens(HttpServletRequest request, HttpServletResponse response) throws UnauthorizedException {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) throw new CookieException("Cookie is missing");

        String accessToken = CookieConfigure.getCookie(cookies, CookieConfigure.COOKIE_OAUTH_ACCESS_TOKEN)
                .orElseGet(() -> {
                    log.warn("cookie with access token doesn't exists");
                    return null;
                });
        String refreshToken = CookieConfigure.getCookie(cookies, CookieConfigure.COOKIE_OAUTH_REFRESH_TOKEN)
                .orElseThrow(() -> new CookieException("cookie with refresh token doesn't exists"));


        AuthTokenStorage tokenStorage = new AuthTokenStorage(accessToken, refreshToken);

        return getClaims(tokenStorage, response);
    }


    private Optional<Claims> extractAllClaims(String token) throws JwtException {
        if (token == null || token.isBlank()) return Optional.empty();

        try {
            return Optional.of(Jwts.parserBuilder()
                    .setSigningKey(decodeKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody());
        } catch (JwtException | IllegalArgumentException ex) {
            return Optional.empty();
        }
    }

    private String generateToken(Map<String, Object> claims, String subject, long expiration) {
        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + expiration))
                .signWith(decodeKey())
                .compact();
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration)
                .orElseThrow(() -> new JwtException("Could not extract expiration date from token"));
    }

    private <R> Optional<R> extractClaim(String token, Function<Claims, R> claimResolver) {
        try {
            Optional<Claims> claims = extractAllClaims(token);
            return claims.map(claimResolver);
        } catch (ExpiredJwtException e) {
            return Optional.empty(); // можно логировать отдельно
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Invalid JWT: {}", e.getMessage());
            throw e;
        }
    }

    private SecretKey decodeKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(hs256SecretKey));
    }
}
