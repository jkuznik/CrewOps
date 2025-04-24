package pl.crewops.security.jwt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.util.AntPathMatcher;
import pl.crewops.model.auth.AuthUser;
import pl.crewops.security.custom.CustomAuthentication;
import pl.crewops.security.custom.CustomAuthenticationManager;
import pl.crewops.security.custom.UserPrincipal;

@SpringJUnitConfig(
        classes = {
            JwtAuthFilter.class,
            JwtService.class,
            JwtExceptionResolver.class,
            UserDetailsService.class,
            CustomAuthenticationManager.class,
            AntPathMatcher.class
        })
class JwtAuthFilterTest {

    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    @Autowired
    private JwtExceptionResolver jwtExceptionResolver;

    @MockitoBean
    private HttpServletRequest request;

    @MockitoBean
    private HttpServletResponse response;

    @MockitoBean
    private FilterChain chain;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @MockitoBean
    private CustomAuthenticationManager customAuthenticationManager;

    @MockitoBean
    private AntPathMatcher antPathMatcher;

    @Test
    void doFilterInternal() throws ServletException, IOException {
        // given
        String fakeToken = "Bearer testToken";
        String username = "TestUser";
        var authUser =
                AuthUser.builder().username(username).roles(new HashSet<>()).build();
        UserPrincipal userPrincipal = new UserPrincipal(authUser);
        CustomAuthentication auth = new CustomAuthentication(userPrincipal);

        // when
        when(request.getRequestURI()).thenReturn("/test-endpoint");
        when(jwtService.extractTokenFromRequest(request)).thenReturn(fakeToken);
        when(jwtService.extractUserFirstName(fakeToken)).thenReturn(username);

        when(userDetailsService.loadUserByUsername(username)).thenReturn(userPrincipal);
        when(jwtService.validateToken(fakeToken, userPrincipal)).thenReturn(true);

        when(customAuthenticationManager.authenticate(any(CustomAuthentication.class)))
                .thenReturn(auth);

        jwtAuthFilter.doFilterInternal(request, response, chain);

        // then
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertEquals(
                userPrincipal,
                SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        verify(chain).doFilter(request, response);
        verify(response, never()).sendError(anyInt(), anyString());
    }
}
