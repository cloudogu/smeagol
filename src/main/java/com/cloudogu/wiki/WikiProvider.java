/**
 * Copyright (c) 2016 Cloudogu GmbH. All Rights Reserved.
 * 
 * Copyright notice
 */

package com.cloudogu.wiki;

import javax.servlet.http.HttpServlet;

/**
 * The provider must implement the main logic to retrieve, push and render wikis.
 * 
 * @author Sebastian Sdorra
 */
public interface WikiProvider {

    /**
     * Return all configured wikis. The method should respect the permissions of the currently authenticated user, if 
     * the provider has an authorization strategy.
     * 
     * @return all configured wikis
     */
    public Iterable<Wiki> getAll();

    /**
     * Returns the {@link HttpServlet} which renders the wiki. This method is used by the {@link WikiDispatcherServlet}
     * before the dispatcher forwards the request to the created servlet. Implementers should consider a caching 
     * strategy, because the methods is called for every request.
     * 
     * @param name name of the wiki
     * 
     * @return servlet to render the wiki with the given name
     */
    public HttpServlet getServlet(String name);
    
    /**
     * Push changes back to the remote repository.
     * 
     * @param wiki name of wiki
     * @param sha1 sha1 hash of the commit
     */
    public void push(String wiki, String sha1);

}
