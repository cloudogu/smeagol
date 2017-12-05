/*
 * Copyright (c) 2016 Cloudogu GmbH. All Rights Reserved.
 * 
 * Copyright notice
 */
package com.cloudogu.wiki;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * 
 * @author Michael Behlendorf
 */
public class CasLogoutServlet extends HttpServlet {

    private static final Logger LOG = LoggerFactory.getLogger(CasLogoutServlet.class);
    private final String logoutUrl;

    public CasLogoutServlet(Map<String,String> casSettings) {
        logoutUrl = casSettings.get("casServerUrlPrefix") + "/logout";
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        request.getSession().invalidate();
        try {
            response.sendRedirect(logoutUrl);
        } catch (IOException ex) {
            LOG.warn("Failed to send redirect.", ex);
        }
    }
}
