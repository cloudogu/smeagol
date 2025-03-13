package com.cloudogu.smeagol.authc.infrastructure;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apereo.cas.client.validation.Cas30ProxyReceivingTicketValidationFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

public class CasFilterWithRedirect extends Cas30ProxyReceivingTicketValidationFilter {
    public CasFilterWithRedirect() {
        super();
    }

    @Override
    protected void onFailedValidation(HttpServletRequest request, HttpServletResponse response) {
        try {
            final String requestUrl = request.getRequestURL().toString();
            final String queryString = request.getQueryString();

            String newQuery = "";
            if (queryString != null) {
                newQuery = Arrays.stream(queryString.split("&"))
                    .filter(param -> !param.startsWith("ticket="))
                    .collect(Collectors.joining("&"));
            }

            final String newUrl = requestUrl + (newQuery.isEmpty() ? "" : "?" + newQuery);
            response.sendRedirect(newUrl);
            throw new TicketEmptyException();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
