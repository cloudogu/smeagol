package com.cloudogu.smeagol;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * The dispatcher decides how to handle a request.
 */
public interface Dispatcher {

    /**
     * Returns {@code true} if the requested uri needs to be dispatched.
     *
     * @param uri requested uri
     *
     * @return {@code true} if the requested uri needs to be dispatched
     */
    boolean needsToBeDispatched(String uri);

    /**
     * Dispatches the request
     *
     * @param request http servlet request
     * @param response http servlet response
     * @param uri requested uri
     *
     * @throws ServletException servlet exception
     * @throws IOException io exception
     */
    void dispatch(HttpServletRequest request, HttpServletResponse response, String uri) throws ServletException, IOException;
}
