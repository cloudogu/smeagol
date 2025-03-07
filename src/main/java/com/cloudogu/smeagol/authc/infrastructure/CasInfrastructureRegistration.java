package com.cloudogu.smeagol.authc.infrastructure;

import com.cloudogu.smeagol.Stage;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSessionListener;
import org.apereo.cas.client.authentication.AuthenticationFilter;
import org.apereo.cas.client.session.SingleSignOutFilter;
import org.apereo.cas.client.session.SingleSignOutHttpSessionListener;
import org.apereo.cas.client.util.HttpServletRequestWrapperFilter;
import org.apereo.cas.client.validation.Assertion;
import org.apereo.cas.client.validation.Cas30ProxyReceivingTicketValidationFilter;
import org.apereo.cas.client.validation.TicketValidationException;
import org.apereo.cas.client.validation.TicketValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.stream.Collectors;

import java.util.Collections;
import java.util.Map;

/**
 * CasInfrastructureRegistration will take the configuration parameters from the application configuration and will
 * construct the required infrastructure for cas authentication.
 */
@Configuration
public class CasInfrastructureRegistration {

    private static final Logger LOG = LoggerFactory.getLogger(CasInfrastructureRegistration.class);

    private Map<String, String> casSettings;

    @Autowired
    public CasInfrastructureRegistration(CasConfiguration configuration, Stage stage) {
        this.casSettings = configuration.createCasSettings();
        if (stage == Stage.DEVELOPMENT) {
            LOG.warn("disable ssl verification for cas communication, because we are in development stage");
            SSL.disableCertificateCheck();
        }
    }

    /**
     * Registers a http session listener, who removes the mapping between http session and cas ticket after a session
     * gets destroyed.
     *
     * @return listener registration.
     */
    @Bean
    public ServletListenerRegistrationBean<HttpSessionListener> singleSignOutListener() {
        ServletListenerRegistrationBean<HttpSessionListener> listener = new ServletListenerRegistrationBean<>();
        listener.setListener(new SingleSignOutHttpSessionListener());
        return listener;
    }

    /**
     * Registers a servlet filter who is responsible for handling single sign out.
     *
     * @return filter registration
     */
    @Bean
    public FilterRegistrationBean singleSignOutFilter() {
        return casFilterRegistration(new SingleSignOutFilter(), 0);
    }

    /**
     * Registers a servlet filter who is responsible for validating received cas tickets.
     *
     * @return filter registration
     */
    @Bean
    public FilterRegistrationBean proxyReceivingTicketValidationFilter() {
        return casFilterRegistration(new CESCas30ProxyReceivingTicketValidationFilter(), 1);
    }

    /**
     * Registers a servlet filter who is responsible for the cas authentication flow.
     *
     * @return filter registration
     */
    @Bean
    public FilterRegistrationBean authenticationFilter() {
        return casFilterRegistration(new AuthenticationFilter(), 2);
    }

    /**
     * Registers a servlet filter that wraps the {@link jakarta.servlet.http.HttpServletRequest} and overrides the
     * authentication related methods.
     *
     * @return filter registration
     */
    @Bean
    public FilterRegistrationBean requestWrapperFilter() {
        return casFilterRegistration(new HttpServletRequestWrapperFilter(), 3);
    }

    private FilterRegistrationBean casFilterRegistration(Filter filter, int order) {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setInitParameters(casSettings);
        registration.setFilter(filter);
        registration.setUrlPatterns(Collections.singleton("/*"));
        registration.setOrder(order);
        return registration;
    }


}
