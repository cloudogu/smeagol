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
        if (isStaticWikiImage(matcher)) {
            handleStaticWikiFile(request, response, matcher);
        } else if (dispatcher.needsToBeDispatched(uri)) {
            dispatcher.dispatch(request, response, uri);
        } else {
            chain.doFilter(request, response);
        }
    }

    private boolean isStaticWikiImage(Matcher matcher) {
        if (matcher.matches()) {
            String repository = matcher.group(1);
            String branch = matcher.group(2);
            String path = matcher.group(3);

            // the static folder from the ui build, contains a media folder which conflicts with the repository image
            // matcher. So we have to ignore matches to the repository "static" with the branch "media".
            // We will also forward the request, if the matching path contains a folder, because webpack generates
            // the media folder always with a flat structure.

            return (!("static".equals(repository) && "media".equals(branch))) || path.contains("/");
        }
        return false;
    }

    private String getPathWithinApplication(HttpServletRequest request) {
        return request.getRequestURI().substring(Strings.nullToEmpty(request.getContextPath()).length());
    }

    private void handleStaticWikiFile(HttpServletRequest request, HttpServletResponse response, Matcher matcher) throws ServletException, IOException {
        String staticWikiFileUri = createStaticWikiFileUri(matcher);
        LOG.debug("forward static file request to {}", staticWikiFileUri);
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
