/**
 * Copyright (c) 2016 Cloudogu GmbH. All Rights Reserved.
 * 
 * Copyright notice
 */

package com.cloudogu.wiki;

import com.cloudogu.wiki.scmm.ScmWikiProvider;
import com.github.sdorra.milieu.Configurations;

/**
 * Application to start the Smeagol Wiki.
 * 
 * @author Sebastian Sdorra
 */
public class App {
   
    /**
     * Reads the configuration from environment and starts the application.
     * 
     * @param args command line args
     * 
     * @throws Exception 
     */
    public static void main(String[] args) throws Exception {
        WikiServerConfiguration configuration = Configurations.get(WikiServerConfiguration.class);
        new WikiServer(new ScmWikiProvider(configuration)).start(configuration);
    }
}
