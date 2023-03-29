package com.cloudogu.smeagol.authc.infrastructure;

import com.cloudogu.smeagol.Stage;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import org.jasig.cas.client.authentication.AuthenticationFilter;
import org.jasig.cas.client.session.SingleSignOutFilter;
import org.jasig.cas.client.session.SingleSignOutHttpSessionListener;
import org.jasig.cas.client.util.HttpServletRequestWrapperFilter;
import org.jasig.cas.client.validation.Cas30ProxyReceivingTicketValidationFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

/**
 * CasInfrastructureRegistration will take the configuration parameters from the application configuration and will
 * construct the required infrastructure for cas authentication.
 */
//@Configuration
public class CasInfrastructureRegistration {

    private static final Logger LOG = LoggerFactory.getLogger(CasInfrastructureRegistration.class);

    private Map<String,String> casSettings;

//    @Autowired
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
        throw new RuntimeException("asdasdasd");
//        ServletListenerRegistrationBean<HttpSessionListener> listener = new ServletListenerRegistrationBean<>();
//        // TODO
////        listener.setListener(new SingleSignOutHttpSessionListener());
//        listener.setListener(new HttpSessionListener() {
//            @Override
//            public void sessionCreated(HttpSessionEvent se) {
//                HttpSessionListener.super.sessionCreated(se);
//            }
//
//            @Override
//            public void sessionDestroyed(HttpSessionEvent se) {
//                HttpSessionListener.super.sessionDestroyed(se);
//            }
//        });
//        return listener;
    }

    /**
     * Registers a servlet filter who is responsible for handling single sign out.
     *
     * @return filter registration
     */
//    @Bean
    public FilterRegistrationBean singleSignOutFilter() {
        throw new RuntimeException("asdasdasd");
//        // TODO
////        return casFilterRegistration(new SingleSignOutFilter(), 0);
//        return casFilterRegistration(new Filter() {
//            @Override
//            public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
//
//            }
//        }, 0);
    }

    /**
     * Registers a servlet filter who is responsible for validating received cas tickets.
     *
     * @return filter registration
     */
//    @Bean
    public FilterRegistrationBean proxyReceivingTicketValidationFilter() {
        throw new RuntimeException("asdasdasd");

        // TODO
////        return casFilterRegistration(new Cas30ProxyReceivingTicketValidationFilter(), 1);
//        return casFilterRegistration(new Filter() {
//            @Override
//            public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
//
//            }
//        }, 1);
    }

    /**
     * Registers a servlet filter who is responsible for the cas authentication flow.
     *
     * @return filter registration
     */
//    @Bean
    public FilterRegistrationBean authenticationFilter() {
        throw new RuntimeException("asdasdasd");
//
//        // TODO
////        return casFilterRegistration(new AuthenticationFilter(), 2);
//        return casFilterRegistration(new Filter() {
//            @Override
//            public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
//
//            }
//        }, 2);
    }

    /**
     * Registers a servlet filter that wraps the {@link javax.servlet.http.HttpServletRequest} and overrides the
     * authentication related methods.
     *
     * @return filter registration
     */
//    @Bean
    public FilterRegistrationBean requestWrapperFilter() {
        throw new RuntimeException("asdasdasd");
//
//        // TODO
////        return casFilterRegistration(new HttpServletRequestWrapperFilter(), 3);
//        return casFilterRegistration(new Filter() {
//            @Override
//            public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
//
//            }
//        }, 3);
    }

    private FilterRegistrationBean casFilterRegistration(Filter filter, int order){
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setInitParameters(casSettings);
        registration.setFilter(filter);
        registration.setUrlPatterns(Collections.singleton("/*"));
        registration.setOrder(order);
        return registration;
    }


}
