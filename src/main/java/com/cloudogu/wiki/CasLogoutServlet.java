/*
 * Copyright (c) 2016 Cloudogu GmbH. All Rights Reserved.
 * 
 * Copyright notice
 */
package com.cloudogu.wiki;

import com.cloudogu.wiki.scmm.RepositoryNotification;
import com.cloudogu.wiki.scmm.ScmWikiProvider;
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

    private String logoutUrl;

    public CasLogoutServlet(Map<String,String> casSettings) {
        logoutUrl = casSettings.get("casServerUrlPrefix") + "/logout";
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.getSession().invalidate();
        response.sendRedirect(logoutUrl);
    }
}
