package com.cloudogu.smeagol.wiki.infrastructure;

import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * Extracts the wildcard part from a request path.
 */
@Component
public class WildcardPathExtractor {

    /**
     * Extract the wildcard part of the request, by using the servlet path to get the url if the endpoint.
     *
     * @param request http servlet request
     * @param base base path of the endpoint
     *
     * @return wildcard part of the request path
     */
    public String extract(HttpServletRequest request, String base) {
        String servletPath = request.getServletPath();

        String path = servletPath.substring(base.length());
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        return path;
    }

}
