/*
 * Copyright (c) 2016 Cloudogu GmbH. All Rights Reserved.
 * 
 * Copyright notice
 */
package com.cloudogu.wiki.scmm;

import com.cloudogu.wiki.Account;
import com.cloudogu.wiki.SessionStore;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Michael Behlendorf
 */
public class NotifyServlet extends HttpServlet {

    private final SessionStore sessions;
    private final ScmWikiProvider provider;

    private final String COMMON_START = "servlet.";

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(NotifyServlet.class);

    public NotifyServlet(SessionStore sessions, ScmWikiProvider provider) {
        this.sessions = sessions;
        this.provider = provider;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        String repositoryToUpdate = request.getParameter("id");
        LOG.info("update repository {}", repositoryToUpdate);
        if (repositoryToUpdate != null && !repositoryToUpdate.isEmpty()) {
            updateRepository(repositoryToUpdate);
        }
    }

    private void updateRepository(String repositoryToUpdate) {
        Iterator<HttpSession> sessionIter = sessions.getAll();
        List updatedDirectories = new ArrayList();
        while (sessionIter.hasNext()) {
            HttpSession s = sessionIter.next();
            checkForServletsInSession(s, updatedDirectories, repositoryToUpdate);
        }
    }

    private void checkForServletsInSession(HttpSession session, List updatedDirectories, String repositoryToUpdate) {
        synchronized (session) {
            Enumeration<String> attributes = session.getAttributeNames();
            Account account = (Account) session.getAttribute("com.cloudogu.wiki.Account");
            while (attributes.hasMoreElements()) {
                String attribute = attributes.nextElement();
                checkIfAttributeIsServlet(updatedDirectories, account, repositoryToUpdate, attribute);
            }
        }

    }

    private void checkIfAttributeIsServlet(List updatedDirectories, Account account, String repositoryToUpdate, String attribute) {
        if (attribute.startsWith(COMMON_START)) {
            String directory = attribute.substring(COMMON_START.length());
            if (directory.startsWith(repositoryToUpdate) && !updatedDirectories.contains(directory)) {
                updateDirectory(directory, updatedDirectories, account);
            }
        }
    }

    private void updateDirectory(String directory, List updatedDirectories, Account account) {
        LOG.info("found repository {} for user {}", directory, account.getUsername());
        File dir = provider.getRepositoryDirectory(directory);
        String branch = provider.getDecodedBranchName(directory);
        try {
            LOG.info("pull changes");
            provider.pullChanges(account, dir, branch);
            updatedDirectories.add(directory);
        } catch (GitAPIException | IOException ex) {
            LOG.warn(null, ex);
        }
    }
}
