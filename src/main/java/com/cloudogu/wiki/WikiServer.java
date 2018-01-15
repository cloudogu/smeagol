/**
 * Copyright (c) 2016 Cloudogu GmbH. All Rights Reserved.
 * 
 * Copyright notice
 */

package com.cloudogu.wiki;

import com.cloudogu.wiki.scmm.NotifyServlet;
import com.cloudogu.wiki.scmm.ScmWikiProvider;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Throwables;
import java.util.EnumSet;
import java.util.Map;
import javax.servlet.DispatcherType;
import javax.servlet.Filter;

import com.mashape.unirest.http.Unirest;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.jasig.cas.client.authentication.AuthenticationFilter;
import org.jasig.cas.client.session.SingleSignOutFilter;
import org.jasig.cas.client.session.SingleSignOutHttpSessionListener;
import org.jasig.cas.client.util.HttpServletRequestWrapperFilter;
import org.jasig.cas.client.validation.Cas30ProxyReceivingTicketValidationFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The server glues filters, servlets and listeners together and starts the application server.
 * 
 * @author Sebastian Sdorra
 */
public class WikiServer {

    private static final Logger LOG = LoggerFactory.getLogger(WikiServer.class);
    
    private final ScmWikiProvider provider;
    private final SessionStore sessions;

    /**
     * Constructs a new server.
     * 
     * @param provider provider which is used to retrieve and render the wikis
     */
    public WikiServer(ScmWikiProvider provider) {
        this.provider = provider;
        this.sessions = new SessionStore();
    }
    
    /**
     * Starts the server with the given configuration.
     * 
     * @param cfg main configuration
     */
    public void start(WikiServerConfiguration cfg) {
        LOG.info(
            "start wiki server on port {} with context path {} in stage {}", 
            cfg.getPort(), cfg.getContextPath(), cfg.getStage()
        );
        configureRestClient(cfg);
        
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath(cfg.getContextPath());
        // set session timeout to 2 hours
        context.getSessionHandler().getSessionManager().setMaxInactiveInterval(7200);
        
        ServletHolder resourceServletHolder = new ServletHolder(DefaultServlet.class);
        resourceServletHolder.setInitParameter("resourceBase", cfg.getStaticPath());
        resourceServletHolder.setInitParameter("pathInfoOnly", "true");
        context.addServlet(resourceServletHolder, "/_static/*");

        Map<String,String> casSettings = cfg.getCasSettings();
        
        // add sso filter
        context.addEventListener(new SingleSignOutHttpSessionListener());
        FilterHolder filter = new FilterHolder(SingleSignOutFilter.class);
        filter.setInitParameters(casSettings);
        context.addFilter(filter, "/*", EnumSet.allOf(DispatcherType.class));
        
        // cas authentication
        casFilter(context, Cas30ProxyReceivingTicketValidationFilter.class, casSettings);
        casFilter(context, AuthenticationFilter.class, casSettings);
        casFilter(context, HttpServletRequestWrapperFilter.class, casSettings);
        
        // listener
        context.getSessionHandler().addEventListener(new SessionListener(sessions));
        
        // context
        FilterHolder contextFilterHolder = new FilterHolder(new WikiContextFilter(cfg, provider, sessions));
        context.addFilter(contextFilterHolder, "/*", EnumSet.allOf(DispatcherType.class));
        
        // rest servlets
        context.addServlet(new ServletHolder(new NotifyServlet(sessions, provider)),"/rest/api/v1/notify");
        
        // main servlet
        context.addServlet(new ServletHolder(new WikiDispatcherServlet(provider, cfg)), "/*");

        context.addServlet(new ServletHolder(new CasLogoutServlet(casSettings)), "/logout");
        context.addServlet(new ServletHolder(new RefreshWikisServlet()), "/refresh");

        Server server = new Server(cfg.getPort());
        server.setHandler(context);
        try {
            server.start();
        } catch (Exception ex) {
            throw Throwables.propagate(ex);
        }
    }

    @VisibleForTesting
    static void configureRestClient(WikiServerConfiguration cfg) {
        HttpClientBuilder httpClientBuilder = HttpClients.custom();
        // Disable cookies to ensure that we always get a fresh scmm session
        httpClientBuilder.disableCookieManagement();
        if ( cfg.getStage() == Stage.DEVELOPMENT ){
            LOG.warn("smeagol is running in development stage, never use this stage for production deployments");
            LOG.warn("disabling ssl/tls certificate checks");
            SSL.disableCertificateCheck(httpClientBuilder);
        }
        Unirest.setHttpClient(httpClientBuilder.build());
    }

    private void casFilter(ServletContextHandler context, Class<? extends Filter> filterClass, Map<String,String> cfg) {
        FilterHolder filter = new FilterHolder(filterClass);
        filter.setInitParameters(cfg);
        context.addFilter(filter, "/*", EnumSet.allOf(DispatcherType.class));
    }
    
}
