package pl.crewops.security.filters;

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
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.util.AntPathMatcher;
import pl.crewops.enums.ControllerURL;
import pl.crewops.model.publicSchema.AuthUser;
import pl.crewops.security.custom.UserPrincipal;
import pl.crewops.security.jwt.JwtExceptionResolver;
import pl.crewops.security.jwt.JwtServiceCore;

@SpringJUnitConfig(
        classes = {
            JwtAuthFilter.class,
            JwtServiceCore.class,
            JwtExceptionResolver.class,
            UserDetailsService.class,
            AntPathMatcher.class,
        })
class JwtAuthFilterTest {

    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    @Autowired
    private JwtExceptionResolver jwtExceptionResolver;

    @MockitoBean
    private JwtServiceCore jwtService;

    @MockitoBean
    private HttpServletRequest request;

    @MockitoBean
    private HttpServletResponse response;

    @MockitoBean
    private FilterChain filterChain;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @MockitoBean
    private AntPathMatcher antPathMatcher;

    @Test
    void shouldDoFilterInternal() throws ServletException, IOException {
        // given
        String fakeToken = "Bearer testToken";
        var username = "TestUser";
        var employeeId = UUID.randomUUID();
        var authUser =
                AuthUser.builder().username(username).roles(new HashSet<>()).build();
        UserPrincipal userPrincipal = new UserPrincipal(authUser);

        // when
        when(request.getRequestURI()).thenReturn("/test-endpoint");
        when(jwtService.extractTokenFromRequest(request)).thenReturn(fakeToken);
        when(jwtService.extractEmployeeId(fakeToken)).thenReturn(employeeId);

        when(userDetailsService.loadUserByUsername(employeeId.toString())).thenReturn(userPrincipal);
        when(jwtService.validToken(fakeToken, userPrincipal)).thenReturn(true);

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        // then
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertEquals(
                userPrincipal,
                SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        verify(filterChain).doFilter(request, response);
        verify(response, never()).sendError(anyInt(), anyString());
    }

    @Test
    void shouldSkipAuthenticationAndProceedWhenRequestIsPublicUrl() throws Exception {
        // given
        String[] strings = ControllerURL.publicUrl();
        String randomPublicUrl = strings[(int) (Math.random() * strings.length)];

        // when
        when(request.getRequestURI()).thenReturn(randomPublicUrl);

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        // then
        verify(filterChain, times(1)).doFilter(request, response);
        verify(jwtService, never()).extractTokenFromRequest(any());
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}
