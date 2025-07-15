package com.example.demo;


import com.example.demo.filter.PasswordEmailTokenFilter;
import com.example.demo.service.impl.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetailsService;

import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class PasswordEmailTokenFilterTest {

    @InjectMocks
    private PasswordEmailTokenFilter passwordEmailFilter;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserDetailsService userDetailsService;

    private HttpServletRequest request;
    private HttpServletResponse response;


    @BeforeEach
    void setUp() {
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
    }

//    @Test
//    void doFilterInternal_validData_doFilter() throws ServletException, IOException {
//        Cookie accessCookie = new Cookie(CookieConfigure.COOKIE_OAUTH_ACCESS_TOKEN, "access_token");
//        Cookie refreshCookie = new Cookie(CookieConfigure.COOKIE_OAUTH_REFRESH_TOKEN, "refresh_token");
//        Cookie[] cookies = new Cookie[] { accessCookie, refreshCookie };
//
//        when(request.getRequestURI()).thenReturn("/sec");
//        when(request.getCookies()).thenReturn(cookies);
//
//        try (MockedStatic<CookieConfigure> conf = Mockito.mockStatic(CookieConfigure.class)) {
//            conf.when(() -> CookieConfigure.getCookie(cookies, CookieConfigure.COOKIE_OAUTH_ACCESS_TOKEN))
//                    .thenReturn(Optional.of("access_token"));
//
//            conf.when(() -> CookieConfigure.getCookie(cookies, CookieConfigure.COOKIE_OAUTH_REFRESH_TOKEN))
//                    .thenReturn(Optional.of("refresh_token"));
//        }
//
//            AuthTokenStorage tokenStorage = new AuthTokenStorage("access", "refresh");
//
//            Claims claims = mock(Claims.class);
//            String email = "someMail@mail123";
//            when(jwtService.getClaims(tokenStorage, response)).thenReturn(claims);
//            when(claims.getSubject()).thenReturn(email);
//
//            UserDetailsImpl userDetails = mock(UserDetailsImpl.class);
//            when(userDetails.getAuthorities()).thenReturn(Collections.emptyList());
//            when(userDetailsService.loadUserByUsername(email)).thenReturn(userDetails);
//
//            SecurityContext context = SecurityContextHolder.createEmptyContext();
//            SecurityContextHolder.setContext(context);
//
//            FilterChain filterChain = mock(FilterChain.class);
//            passwordEmailFilter.doFilter(request, response, filterChain);
//
//            assertNotNull(SecurityContextHolder.getContext().getAuthentication());
//            assertEquals(userDetails, SecurityContextHolder.getContext().getAuthentication().getPrincipal());
//
//            verify(jwtService).getClaims(eq(tokenStorage), eq(response));
//            verify(claims).getSubject();
//            verify(userDetailsService).loadUserByUsername(email);
//            verify(filterChain).doFilter(request, response);
//    }


}
