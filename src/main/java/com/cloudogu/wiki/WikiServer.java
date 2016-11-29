/**
 * Copyright (c) 2016 Cloudogu GmbH. All Rights Reserved.
 * 
 * Copyright notice
 */

package com.cloudogu.wiki;

import com.google.common.base.Throwables;
import java.util.EnumSet;
import java.util.Map;
import javax.servlet.DispatcherType;
import javax.servlet.Filter;
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
    
    private final WikiProvider provider;

    /**
     * Constructs a new server.
     * 
     * @param provider provider which is used to retrieve and render the wikis
     */
    public WikiServer(WikiProvider provider) {
        this.provider = provider;
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
        
        if ( cfg.getStage() == Stage.DEVELOPMENT ){
            LOG.warn("smeagol is running in development stage, never use this stage for production deployments");
            LOG.warn("disabling ssl/tls certificate checks");
            SSL.disableCertificateCheck();
        }
        
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath(cfg.getContextPath());
        // set session timeout to 30 minutes
        context.getSessionHandler().getSessionManager().setMaxInactiveInterval(1800);
        
        ServletHolder resourceServletHolder = new ServletHolder(DefaultServlet.class);
        resourceServletHolder.setInitParameter("resourceBase", cfg.getStaticPath());
        resourceServletHolder.setInitParameter("pathInfoOnly", "true");
        context.addServlet(resourceServletHolder, "/_static/*");

        Map<String,String> casSettings = cfg.getCasSettings();
        
        // add sso filter
        context.addEventListener(new SingleSignOutHttpSessionListener());
        FilterHolder filter = new FilterHolder(SingleSignOutFilter.class);
        filter.setInitParameters(casSettings);
        context.addFilter(filter, "/", EnumSet.allOf(DispatcherType.class));
        
        // cas authentication
        casFilter(context, Cas30ProxyReceivingTicketValidationFilter.class, casSettings);
        casFilter(context, AuthenticationFilter.class, casSettings);
        casFilter(context, HttpServletRequestWrapperFilter.class, casSettings);
        
        // context
        FilterHolder contextFilterHolder = new FilterHolder(new WikiContextFilter(cfg, provider));
        context.addFilter(contextFilterHolder, "/*", EnumSet.allOf(DispatcherType.class));
        
        // main servlet
        context.addServlet(new ServletHolder(new WikiDispatcherServlet(provider, cfg)), "/*");

        Server server = new Server(cfg.getPort());
        server.setHandler(context);
        try {
            server.start();
        } catch (Exception ex) {
            throw Throwables.propagate(ex);
        }
    }
    
    private void casFilter(ServletContextHandler context, Class<? extends Filter> filterClass, Map<String,String> cfg) {
        FilterHolder filter = new FilterHolder(filterClass);
        filter.setInitParameters(cfg);
        context.addFilter(filter, "/*", EnumSet.allOf(DispatcherType.class));
    }
    
}
