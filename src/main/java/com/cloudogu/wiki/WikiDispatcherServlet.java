/**
 * Copyright (c) 2016 Cloudogu GmbH. All Rights Reserved.
 *
 * Copyright notice
 */
package com.cloudogu.wiki;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Strings;
import java.io.IOException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The dispatcher servlet takes the current requested name of the wiki and forwards the request to the {@link Servlet},
 * which was created with {@link WikiProvider}. If the root application was requested an overview of all available wikis
 * is created from the {@link WikiProvider}.
 *
 * @author Sebastian Sdorra
 */
public class WikiDispatcherServlet extends HttpServlet {

    private static final Logger LOG = LoggerFactory.getLogger(WikiDispatcherServlet.class);

    private final MustacheFactory factory = new DefaultMustacheFactory();

    private static final long serialVersionUID = 7511937785395456331L;

    private final WikiProvider provider;

    public WikiDispatcherServlet(WikiProvider provider) {
        this.provider = provider;
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String name = getWikiName(req);
        if (Strings.isNullOrEmpty(name)) {
            renderOverview(req, resp);
        } else {
            try {
                Servlet servlet = provider.getServlet(name);
                if (servlet != null) {
                    servlet.service(wrap(req), resp);
                } else {
                    renderNotFound(req, resp);
                }
            } catch (WikiNotFoundException ex) {
                LOG.trace("could not find wiki", ex);
                renderNotFound(req, resp);
            }
        }
    }

    private void renderNotFound(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setStatus(404);
        renderTemplate(response, "notfound.html", new NotFound(request));
    }

    private void renderOverview(HttpServletRequest request, HttpServletResponse response) throws IOException {
        renderTemplate(response, "overview.html", new Overview(request, provider.getAll()));
    }

    private void renderTemplate(HttpServletResponse response, String tpl, Object ctx) throws IOException {
        response.setContentType("text/html");
        Mustache mustache = factory.compile(WikiResources.path(tpl));
        mustache.execute(response.getWriter(), ctx);
    }

    private HttpServletRequest wrap(HttpServletRequest request) {
        return new DispatchHttpServletRequestWrapper(request);
    }

    @VisibleForTesting
    String getWikiName(HttpServletRequest request) {
        String path = request.getPathInfo();
        if (Strings.isNullOrEmpty(path)) {
            return null;
        }
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        int index = path.indexOf("/");
        if (index > 0) {
            return path.substring(0, index);
        }
        return path;
    }

    private static class DispatchHttpServletRequestWrapper extends HttpServletRequestWrapper {

        public DispatchHttpServletRequestWrapper(HttpServletRequest request) {
            super(request);
        }

        @Override
        public String getServletPath() {
            return super.getContextPath() + super.getServletPath();
        }

    }

    private static class NotFound {

        private final HttpServletRequest request;

        public NotFound(HttpServletRequest request) {
            this.request = request;
        }

        public HttpServletRequest getRequest() {
            return request;
        }

    }

    private static class Overview {

        private final HttpServletRequest request;
        private final Iterable<Wiki> wikis;

        public Overview(HttpServletRequest request, Iterable<Wiki> wikis) {
            this.request = request;
            this.wikis = wikis;
        }

        public HttpServletRequest getRequest() {
            return request;
        }

        public Iterable<Wiki> getWikis() {
            return wikis;
        }
    }

}
