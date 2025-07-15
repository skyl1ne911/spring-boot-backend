package com.example.demo.configuration;



import com.example.demo.controller.SecurityController;
import com.example.demo.filter.PasswordEmailTokenFilter;
import com.example.demo.filter.OAuth2TokenFilter;
import com.example.demo.security.oauth2.OAuth2SuccessHandler;
import com.example.demo.security.logout.CustomLogoutSuccessHandler;
import com.example.demo.security.oauth2.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.FormHttpMessageConverter;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2RefreshTokenGrantRequest;
import org.springframework.security.oauth2.client.endpoint.RestClientRefreshTokenTokenResponseClient;
import org.springframework.security.oauth2.client.http.OAuth2ErrorResponseErrorHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.*;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.web.client.RestClient;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    public final static String URI_FRONTEND_CORS = "https://localhost/web/";

    @Autowired
    private OAuth2TokenFilter OAuth2TokenFilter;

    @Autowired
    private PasswordEmailTokenFilter passwordEmailFilter;

    @Autowired
    private ClientRegistrationRepository clientRegistrationRepository;

    @Autowired
    private CustomAuthorizedClientService authorizedClientService;

    private UserDetailsService userDetailsService;

    @Autowired
    public void setUserDetailsService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(CsrfConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfig()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(SecurityController.URI_SECURED + "/**").authenticated()
                        .anyRequest().permitAll()
                )
                .sessionManagement(config -> config
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS).enableSessionUrlRewriting(false)
                )
                .exceptionHandling(handle ->
                        handle.authenticationEntryPoint((request, response, authException) -> {
                            response.sendRedirect(URI_FRONTEND_CORS);
                        })
                )
                .oauth2Login(oauth2 -> {
                         oauth2.authorizationEndpoint(config -> {
                             config.authorizationRequestResolver(
                                     authorizationRequestResolver(clientRegistrationRepository));
                             config.authorizationRequestRepository(authorizationRequest());
                         });
                         oauth2.authorizedClientService(authorizedClientService);
                         oauth2.successHandler(oauth2Handler());
                         oauth2.failureHandler(OAuth2Handler::oauthFailureHandler);
                    }
                )
                .logout(logout -> {
                        logout.logoutUrl("/sec/logout");
                        logout.logoutSuccessHandler(logoutSuccessHandler());
                    }
                )
                .addFilterBefore(passwordEmailFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(OAuth2TokenFilter, OAuth2LoginAuthenticationFilter.class);

        return http.build();
    }

     @Bean
     public AuthenticationSuccessHandler oauth2Handler() {
        return new OAuth2SuccessHandler();
     }

    @Bean
    public AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRequest() {
        return new OAuth2RequestRepositoryStateless();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    private AuthenticationProvider authProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());

        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(authProvider());
    }


    @Bean
    public OAuth2AccessTokenResponseClient<OAuth2RefreshTokenGrantRequest> accessTokenResponseClient() {
        return new RestClientRefreshTokenTokenResponseClient();
    }

    @Bean
    public OAuth2AuthorizedClientManager authorizedClientManager(
            ClientRegistrationRepository clientRegistrationRepository,
            OAuth2AuthorizedClientService authorizedClientService) {

        OAuth2AuthorizedClientProvider authorizedClientProvider = OAuth2AuthorizedClientProviderBuilder
                .builder()
                .refreshToken()
                .build();

        AuthorizedClientServiceOAuth2AuthorizedClientManager authorizedClientManager = new
                AuthorizedClientServiceOAuth2AuthorizedClientManager(
                        clientRegistrationRepository, authorizedClientService
        );

        authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);

        return authorizedClientManager;
    }

    private OAuth2AuthorizationRequestResolver oauth2ClientResolver(ClientRegistrationRepository clientRepository) {
        DefaultOAuth2AuthorizationRequestResolver defaultResolver = new DefaultOAuth2AuthorizationRequestResolver(
                clientRepository, "/oauth2/authorization");

        defaultResolver.setAuthorizationRequestCustomizer(builder -> {
            builder.state("123xyz456");
        });
        return defaultResolver;
    }

    @Bean
    public LogoutSuccessHandler logoutSuccessHandler() {
        return new CustomLogoutSuccessHandler(this.clientRegistrationRepository);
    }

//    for exchange auth grant for an access token
//    @Bean
//    public OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> accessTokenResponseClient() {
//        RestClientAuthorizationCodeTokenResponseClient accessTokenResponseClient =
//                new RestClientAuthorizationCodeTokenResponseClient();
//        accessTokenResponseClient.setRestClient(restClientBuilder());
//        return accessTokenResponseClient;
//    }


    private RestClient restClientBuilder() {
        return RestClient.builder()
                .messageConverters(messageConverters -> {
                    messageConverters.clear();
                    messageConverters.add(new FormHttpMessageConverter());
                    messageConverters.add(new OAuth2AccessTokenResponseHttpMessageConverter());
                })
                .defaultStatusHandler(new OAuth2ErrorResponseErrorHandler())
                .build();
    }


    private CorsConfigurationSource corsConfig() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(URI_FRONTEND_CORS));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);


        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }


    private OAuth2AuthorizationRequestResolver authorizationRequestResolver(ClientRegistrationRepository clientRegistrationRepository) {
        DefaultOAuth2AuthorizationRequestResolver requestResolver = new DefaultOAuth2AuthorizationRequestResolver(
                clientRegistrationRepository, "/oauth2/authorization"
        );

        requestResolver.setAuthorizationRequestCustomizer(authorizationRequestCustomizer());
        return requestResolver;
    }

    private Consumer<OAuth2AuthorizationRequest.Builder> authorizationRequestCustomizer() {
        return customizer -> customizer
                .additionalParameters(Map.of("prompt", "none", "access_type", "offline"));
    }

}


