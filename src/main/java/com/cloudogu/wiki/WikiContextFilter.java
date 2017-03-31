/**
 * Copyright (c) 2016 Cloudogu GmbH. All Rights Reserved.
 * 
 * Copyright notice
 */

package com.cloudogu.wiki;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This filter initializes and destroys the {@link WikiContext} by using the {@link WikiContextFactory},
 * 
 * @author Sebastian Sdorra
 */
public class WikiContextFilter implements Filter {

    private final WikiServerConfiguration configuration;
    private final WikiProvider provider;
    private final SessionStore sessions;

    /**
     * Constructs a new WikiContextFilter.
     * 
     * @param configuration main application configuration
     * @param provider wiki context provider
     * @param sessions session store
     */
    public WikiContextFilter(WikiServerConfiguration configuration, WikiProvider provider, SessionStore sessions) {
        this.configuration = configuration;
        this.provider = provider;
        this.sessions = sessions;
    }
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        WikiContextFactory factory = WikiContextFactory.getInstance();
        factory.begin(
            new WikiContext(configuration, provider, (HttpServletRequest)request, (HttpServletResponse)response, sessions)
        );
        try {
            chain.doFilter(request, response);
        } finally {
            factory.end();
        }
    }

    @Override
    public void destroy() {
        
    }

}
