package com.cloudogu.smeagol.authc.infrastructure;

import jakarta.servlet.*;
import org.apereo.cas.client.validation.Cas30ProxyReceivingTicketValidationFilter;

import java.io.IOException;

public class CESCas30ProxyReceivingTicketValidationFilter implements Filter {
    private final Cas30ProxyReceivingTicketValidationFilter original;

    public CESCas30ProxyReceivingTicketValidationFilter() {
        super();
        this.original = new CasFilterWithRedirect();
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
            // Ignore that exception. The exception is just thrown to prevent normal filter flow after the redirect is called
        }
    }

    @Override
    public void destroy() {
        this.original.destroy();
    }
}
