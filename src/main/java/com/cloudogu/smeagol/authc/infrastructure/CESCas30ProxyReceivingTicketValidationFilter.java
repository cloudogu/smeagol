package com.cloudogu.smeagol.authc.infrastructure;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apereo.cas.client.validation.Cas30ProxyReceivingTicketValidationFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

public class CESCas30ProxyReceivingTicketValidationFilter implements Filter {
    private final Cas30ProxyReceivingTicketValidationFilter original;

    public CESCas30ProxyReceivingTicketValidationFilter() {
        super();
        this.original = new Cas30ProxyReceivingTicketValidationFilterWithoutException();
    }

    public static class Cas30ProxyReceivingTicketValidationFilterWithoutException extends Cas30ProxyReceivingTicketValidationFilter {
        public Cas30ProxyReceivingTicketValidationFilterWithoutException() {
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
                System.out.println("=================>>");
                System.out.println("!=================>>");
                System.out.println("=================>>");
                System.out.println(newUrl);
                response.sendRedirect(newUrl);
                throw new TicketEmptyException();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.original.init(filterConfig);
        this.original.setExceptionOnValidationFailure(false);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        try {
            this.original.doFilter(servletRequest, servletResponse, filterChain);
        }
        catch (TicketEmptyException e){
            return;
        }
    }

    @Override
    public void destroy() {
        this.original.destroy();
    }
}
