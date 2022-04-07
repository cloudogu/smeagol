package com.cloudogu.smeagol.authc.infrastructure;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SignOutControllerTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpSession session;

    @Mock
    private CasConfiguration casConfiguration;

    @InjectMocks
    private SignOutController controller;

    @Before
    public void setUp() {
        when(casConfiguration.getLogoutUrl()).thenReturn("/cas/logout");
    }

    @Test
    public void testLogout() {
        when(request.getSession(false)).thenReturn(session);
        ResponseEntity<Void> entity = controller.logout(request);

        verify(session).invalidate();

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.TEMPORARY_REDIRECT);
        assertThat(entity.getHeaders().get(HttpHeaders.LOCATION)).contains("/cas/logout");
    }

    @Test
    public void testLogoutWithoutSession() {
        ResponseEntity<Void> entity = controller.logout(request);

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.TEMPORARY_REDIRECT);
        assertThat(entity.getHeaders().get(HttpHeaders.LOCATION)).contains("/cas/logout");
    }

}
