package com.example.demo.filter;

import com.example.demo.configuration.CookieConfigure;
import com.example.demo.exception.CookieException;
import com.example.demo.exception.UnauthorizedException;
import com.example.demo.mapper.AuthTokenStorage;
import com.example.demo.service.impl.JwtService;
import com.example.demo.service.impl.UserDetailsImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.DataException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.rememberme.CookieTheftException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Objects;

@Slf4j
@Component
public class PasswordEmailTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (request.getRequestURI().startsWith("/public")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            Claims claims = jwtService.extractClaimsAndTokens(request, response);

            String email = claims.getSubject();
            String authType = claims.get(JwtService.CLAIM_KEY_AUTH_TYPE).toString();


            if (Objects.equals(authType, JwtService.AUTH_LOCAL) && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetailsImpl userDetails = (UserDetailsImpl) userDetailsService.loadUserByUsername(email);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.info("The user has been successfully authenticated");
            }

        } catch (UnauthorizedException | JwtException e) {
//            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            response.setContentType("application/json");
//            response.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
//            response.flushBuffer();
            log.warn(e.getMessage());
        }
        finally {
            filterChain.doFilter(request, response);
        }
    }
}
