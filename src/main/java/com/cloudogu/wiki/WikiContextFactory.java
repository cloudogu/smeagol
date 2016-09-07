/**
 * Copyright (c) 2016 Cloudogu GmbH. All Rights Reserved.
 * 
 * Copyright notice
 */

package com.cloudogu.wiki;

/**
 * Factory class for the {@link WikiContext}. The factory stores the instance in a {@link ThreadLocal} store. With this
 * approach a {@link WikiContext} could be retrieved from every part of the application.
 * 
 * @author Sebastian Sdorra
 */
public class WikiContextFactory {

    private static final WikiContextFactory INSTANCE = new WikiContextFactory();
    
    public static WikiContextFactory getInstance(){
        return INSTANCE;
    }
    
    private final ThreadLocal<WikiContext> contextStore = new ThreadLocal<>();
    
    /**
     * Initializes the wiki context store. This method should only be called from the {@link WikiContextFilter}.
     * 
     * @param context 
     */
    void begin(WikiContext context){
        contextStore.set(context);
    }
    
    /**
     * Returns the instance of {@link WikiContext}, which is currently bound to the thread.
     * 
     * @return context instance of thread
     */
    public WikiContext get(){
        return contextStore.get();
    }
    
    /**
     * Removes the context instance from thread. This method should only be called from the {@link WikiContextFilter}.
     */
    void end(){
        contextStore.remove();
    }
}
