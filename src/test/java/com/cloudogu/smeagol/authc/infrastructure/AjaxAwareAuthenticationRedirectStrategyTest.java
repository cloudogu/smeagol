package com.cloudogu.smeagol.authc.infrastructure;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.cloudogu.smeagol.authc.infrastructure.AjaxAwareAuthenticationRedirectStrategy.AJAX_HEADER;
import static com.cloudogu.smeagol.authc.infrastructure.AjaxAwareAuthenticationRedirectStrategy.AJAX_HEADER_VALUE;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AjaxAwareAuthenticationRedirectStrategyTest {

    private static final String REDIRECT_TARGET = "http://hitchhicker.com";

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    private final AjaxAwareAuthenticationRedirectStrategy redirectStrategy = new AjaxAwareAuthenticationRedirectStrategy();

    @Test
    public void testRedirect() throws IOException {
        redirectStrategy.redirect(request, response, REDIRECT_TARGET);

        verify(response).sendRedirect(REDIRECT_TARGET);
    }

    @Test
    public void testRedirectWithAjaxRequest() throws IOException {
        when(request.getHeader(AJAX_HEADER)).thenReturn(AJAX_HEADER_VALUE);

        redirectStrategy.redirect(request, response, REDIRECT_TARGET);

        verify(response).setStatus(HttpStatus.UNAUTHORIZED.value());
        verify(response).setHeader(HttpHeaders.LOCATION, REDIRECT_TARGET);
    }

}
