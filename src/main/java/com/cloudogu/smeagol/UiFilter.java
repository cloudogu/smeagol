package com.cloudogu.smeagol;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.regex.Pattern;

/**
 * The ui filter dispatches every ui request to the index html in order to support html5 routing.
 *
 * TODO images stored in wiki
 */
public class UiFilter implements Filter {

    private static final Logger LOG = LoggerFactory.getLogger(UiFilter.class);

    private static final Pattern NON_UI_PREFIX = Pattern.compile("/?(api|locales|static)/.*");

    private static final Pattern STATIC_ROOT_FILES = Pattern.compile("/?[^/]+\\.[^/]+");

    private final UrlPathHelper urlPathHelper = new UrlPathHelper();

    @Override
    public void init(FilterConfig filterConfig) {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = cast(servletRequest);
        HttpServletResponse response = cast(servletResponse);

        String uri = urlPathHelper.getPathWithinApplication(request);
        if (isNonUiRequest(uri)) {
            chain.doFilter(request, response);
        } else {
            LOG.trace("forward ui request {} to /index.html", uri);
            RequestDispatcher dispatcher = request.getRequestDispatcher("/index.html");
            dispatcher.forward(request, response);
        }
    }

    private boolean isNonUiRequest(String uri) {
        return isRoot(uri) || hasNonUiPrefix(uri) || isStaticRootFile(uri);
    }

    private boolean isRoot(String uri) {
        return uri.isEmpty() || uri.equals("/");
    }

    private boolean hasNonUiPrefix(String uri) {
        return NON_UI_PREFIX.matcher(uri).matches();
    }

    private boolean isStaticRootFile(String uri) {
        return STATIC_ROOT_FILES.matcher(uri).matches();
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

    }
}
