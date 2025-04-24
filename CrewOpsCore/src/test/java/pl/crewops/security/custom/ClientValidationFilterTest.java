package pl.crewops.security.custom;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import pl.crewops.enums.ControllerURL;
import pl.crewops.security.config.PasswordEncoderConfig;
import pl.crewops.security.config.SecurityConfigProperties;

@SpringJUnitConfig(
        classes = {
            ClientValidationFilter.class,
            SecurityConfigProperties.class,
            PasswordEncoder.class,
            AntPathRequestMatcher.class,
            PasswordEncoderConfig.class
        })
class ClientValidationFilterTest {

    @Autowired
    private ClientValidationFilter clientValidationFilter;

    @Autowired
    private SecurityConfigProperties securityConfigProperties;

    @MockitoBean
    private AntPathRequestMatcher antPathRequestMatcher;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private HttpServletRequest request;

    @MockitoBean
    private HttpServletResponse response;

    @MockitoBean
    private FilterChain filterChain;

    @Test
    void doFilterInternal() throws ServletException, IOException {
        // when
        when(request.getRequestURI()).thenReturn("/test-endpoint");
        when(request.getHeader("Client-Id")).thenReturn("a");
        when(passwordEncoder.matches("a", securityConfigProperties.getClientId()))
                .thenReturn(true);

        clientValidationFilter.doFilterInternal(request, response, filterChain);

        // then
        verify(filterChain, times(1)).doFilter(request, response);
        verify(response, never()).sendError(anyInt(), anyString());
    }

    @Test
    void shouldSkipAuthenticationAndProceedWhenRequestIsPublicUrl() throws Exception {
        // given
        String[] strings = ControllerURL.publicUrl();
        String randomPublicUrl = strings[(int) (Math.random() * strings.length)];

        // when
        when(request.getRequestURI()).thenReturn(randomPublicUrl);

        clientValidationFilter.doFilterInternal(request, response, filterChain);

        // then
        verify(filterChain, times(1)).doFilter(request, response);
        verify(passwordEncoder, never()).matches(any(), any());
    }
}
