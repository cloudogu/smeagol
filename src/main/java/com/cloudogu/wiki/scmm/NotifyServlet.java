/*
 * Copyright (c) 2016 Cloudogu GmbH. All Rights Reserved.
 * 
 * Copyright notice
 */
package com.cloudogu.wiki.scmm;

import com.cloudogu.wiki.SessionStore;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Michael Behlendorf
 */
public class NotifyServlet extends HttpServlet {

    private final RepositoryNotification notification;

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(NotifyServlet.class);

    public NotifyServlet(SessionStore sessions, ScmWikiProvider provider) {
        notification = new RepositoryNotification(sessions, provider);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        String repositoryToUpdate = request.getParameter("id");
        LOG.info("update repository {}", repositoryToUpdate);
        if (repositoryToUpdate != null && !repositoryToUpdate.isEmpty()) {
            notification.notifyForRepository(repositoryToUpdate);
        }
    }
}
