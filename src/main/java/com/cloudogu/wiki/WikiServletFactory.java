/**
 * Copyright (c) 2016 Cloudogu GmbH. All Rights Reserved.
 * 
 * Copyright notice
 */

package com.cloudogu.wiki;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Throwables;
import com.squareup.rack.jruby.JRubyRackApplication;
import com.squareup.rack.servlet.RackServlet;
import java.io.File;
import java.io.IOException;
import javax.servlet.http.HttpServlet;
import org.jruby.embed.LocalContextScope;
import org.jruby.embed.ScriptingContainer;
import org.jruby.runtime.builtin.IRubyObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Creates the ruby wrapper servlet for the gollum wiki.
 * 
 * @author Sebastian Sdorra
 */
public class WikiServletFactory {

    private static final Logger LOG = LoggerFactory.getLogger(WikiServletFactory.class);

    private static final String DEFAULT_RUNNER_SCRIPT = "runner.rb";
    
    private final WikiServerConfiguration configuration;
    private final String runnerScript;

    /**
     * Constructs the WikiServletFactory with the default runner script.
     * 
     * @param configuration main configuration
     */
    public WikiServletFactory(WikiServerConfiguration configuration) {
        this(configuration, DEFAULT_RUNNER_SCRIPT);
    }
    
    /**
     * Constructs the WikiServletFactory with the given runner script. This method is only for testing purposes.
     * 
     * @param configuration main configuration
     * @param runnnerScript runner script
     */
    @VisibleForTesting
    WikiServletFactory(WikiServerConfiguration configuration, String runnnerScript) {
        this.configuration = configuration;
        this.runnerScript = runnnerScript;
    }
    
    private IRubyObject createApplication(Wiki wiki, WikiOptions options) throws IOException {
        LOG.debug("create wiki servlet for {}", wiki.getName());
        ScriptingContainer container = new ScriptingContainer(LocalContextScope.THREADSAFE);
        container.getEnvironment().put("GEM_PATH", configuration.getGemPath());
        container.put("wiki", wiki);
        container.put("wikiOptions", options);
        container.put("wikiContextFactory", WikiContextFactory.getInstance());
        return container.parse(WikiResources.read(runnerScript)).run();
    }

    /**
     * Creates the servlet for the wiki with the given options.
     * 
     * @param wiki wiki
     * @param options wiki options
     * 
     * @return creates a new wiki servlet
     */
    public HttpServlet create(Wiki wiki, WikiOptions options) {
        try {
            return new RackServlet(new JRubyRackApplication(createApplication(wiki, options)));
        } catch (IOException ex) {
            throw Throwables.propagate(ex);
        }
    }

}
