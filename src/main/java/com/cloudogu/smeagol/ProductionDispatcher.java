package com.cloudogu.smeagol;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.regex.Pattern;

/**
 * Production dispatcher forwards ui requests to the index.html to support html5 history api.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/API/History">History API</a>
 */
public class ProductionDispatcher implements Dispatcher {

    private static final Logger LOG = LoggerFactory.getLogger(ProductionDispatcher.class);

    private static final Pattern NON_UI_PREFIX = Pattern.compile("/?(api|locales|static)/.*");
    private static final Pattern STATIC_ROOT_FILES = Pattern.compile("/?[^/]+\\.[^/]+");

    @Override
    public boolean needsToBeDispatched(String uri) {
        return !(isRoot(uri) || hasNonUiPrefix(uri) || isStaticRootFile(uri));
    }

    @Override
    public void dispatch(HttpServletRequest request, HttpServletResponse response, String uri) throws ServletException, IOException {
        LOG.trace("forward ui request {} to /index.html", uri);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/index.html");
        dispatcher.forward(request, response);
    }

    private boolean isRoot(String uri) {
        return uri.isEmpty() || "/".equals(uri);
    }

    private boolean hasNonUiPrefix(String uri) {
        return NON_UI_PREFIX.matcher(uri).matches();
    }

    private boolean isStaticRootFile(String uri) {
        return STATIC_ROOT_FILES.matcher(uri).matches();
    }

}
