/**
 * Copyright (c) 2016 Cloudogu GmbH. All Rights Reserved.
 * 
 * Copyright notice
 */


package com.cloudogu.wiki;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * The WikiContext is used to call methods and retrieve central informations from every place in the application.
 * A context is created per request and can be retrieved by using {@link WikiContextFactory#get()}.
 * 
 * @author Sebastian Sdorra
 */
public class WikiContext {
    
    private final WikiServerConfiguration configuration;
    private final WikiProvider provider;
    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private final SessionStore sessions;

    /**
     * Constructs a new WikiContext.
     * 
     * @param configuration main application configuration
     * @param provider wiki provider implementation
     * @param request http request
     * @param response http response
     * @param sessions session store
     */
    public WikiContext(WikiServerConfiguration configuration, WikiProvider provider, HttpServletRequest request, HttpServletResponse response, SessionStore sessions) {
        this.configuration = configuration;
        this.provider = provider;
        this.request = request;
        this.response = response;
        this.sessions = sessions;
    }
    
    public SessionStore getSessions(){
        return sessions;
    }
    
    public HttpServletRequest getRequest() {
        return request;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public WikiProvider getProvider() {
        return provider;
    }
    
    public Account getAccount(){
        return Accounts.fromRequest(configuration, request);
    }
    
}
