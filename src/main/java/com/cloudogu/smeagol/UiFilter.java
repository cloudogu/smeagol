package com.cloudogu.smeagol;

import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The ui filter dispatches every ui request to the index html in order to support html5 routing.
 */
public class UiFilter implements Filter {

    private static final Logger LOG = LoggerFactory.getLogger(UiFilter.class);

    private static final String STATIC_WIKI_FILE_URL_TEMPLATE = "/api/v1/repositories/%s/branches/%s/static/%s";
    private static final Pattern STATIC_WIKI_IMAGE = Pattern.compile("(?i)/?([^/]+)/([^/]+)/(.*\\.(gif|jpg|jpeg|tiff|png|svg|webp))");

    private final Dispatcher dispatcher;

    @Autowired
    public UiFilter(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @Override
    public void init(FilterConfig filterConfig) {
        // nothing to initialize
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = cast(servletRequest);
        HttpServletResponse response = cast(servletResponse);

        String uri = getPathWithinApplication(request);
        Matcher matcher = STATIC_WIKI_IMAGE.matcher(uri);
        if (matcher.matches()) {
            handleStaticWikiFile(request, response, matcher);
        } else if (dispatcher.needsToBeDispatched(uri)) {
            dispatcher.dispatch(request, response, uri);
        } else {
            chain.doFilter(request, response);
        }
    }

    private String getPathWithinApplication(HttpServletRequest request) {
        return request.getRequestURI().substring(Strings.nullToEmpty(request.getContextPath()).length());
    }

    private void handleStaticWikiFile(HttpServletRequest request, HttpServletResponse response, Matcher matcher) throws ServletException, IOException {
        String staticWikiFileUri = createStaticWikiFileUri(matcher);
        LOG.trace("forward static file request to {}", staticWikiFileUri);
        RequestDispatcher requestDispatcher = request.getRequestDispatcher(staticWikiFileUri);
        requestDispatcher.forward(request, response);
    }

    private String createStaticWikiFileUri(Matcher matcher) {
        String repository = matcher.group(1);
        String branch = matcher.group(2);
        String path = matcher.group(3);

        return String.format(STATIC_WIKI_FILE_URL_TEMPLATE, repository, branch, path);
    }

    private HttpServletResponse cast(ServletResponse servletResponse) throws ServletException {
        if (servletResponse instanceof HttpServletResponse) {
            return (HttpServletResponse) servletResponse;
        }
        throw new ServletException("HttpServletResponse is required");
    }

    private HttpServletRequest cast(ServletRequest servletRequest) throws ServletException {
        if (servletRequest instanceof HttpServletRequest) {
            return (HttpServletRequest) servletRequest;
        }
        throw new ServletException("HttpServletRequest is required");
    }

    @Override
    public void destroy() {
        // nothing to close or destroy
    }
}
