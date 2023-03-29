package com.cloudogu.smeagol.authc.infrastructure;

import com.google.common.annotations.VisibleForTesting;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apereo.cas.client.authentication.AuthenticationRedirectStrategy;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import java.io.IOException;

/**
 * Cas authentication redirect strategy, which returns a normal redirect for browser, but for ajax request it will
 * respond with status unauthorized and the location header.
 */
public class AjaxAwareAuthenticationRedirectStrategy implements AuthenticationRedirectStrategy {

    @VisibleForTesting
    static final String AJAX_HEADER = "X-Requested-With";

    @VisibleForTesting
    static final String AJAX_HEADER_VALUE = "XMLHttpRequest";

    @Override
    public void redirect(HttpServletRequest request, HttpServletResponse response, String potentialRedirectUrl) throws IOException {
        if (isAjaxRequest(request)) {
            sendAjaxResponse(response, potentialRedirectUrl);
        } else {
            sendNormalResponse(response, potentialRedirectUrl);
        }
    }

    private boolean isAjaxRequest(HttpServletRequest request) {
        return AJAX_HEADER_VALUE.equalsIgnoreCase(request.getHeader(AJAX_HEADER));
    }

    private void sendAjaxResponse(HttpServletResponse response, String potentialRedirectUrl) {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setHeader(HttpHeaders.LOCATION, potentialRedirectUrl);
    }

    private void sendNormalResponse(HttpServletResponse response, String potentialRedirectUrl) throws IOException {
        response.sendRedirect(potentialRedirectUrl);
    }

}
