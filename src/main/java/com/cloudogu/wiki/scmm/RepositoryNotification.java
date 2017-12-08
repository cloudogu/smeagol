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
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.servlet.http.HttpSession;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.slf4j.LoggerFactory;

/**
 * RepositoryNotification searches for open wikis via sessions and updates them 
 * for a given repository.
 * 
 * @author Michael Behlendorf
 */
public class RepositoryNotification {
    
    private final SessionStore sessions;
    private final ScmWikiProvider provider;
    private String repositoryToUpdate;
    private final Set<String> updatedDirectories = new HashSet<>();
    private final String COMMON_START = "servlet.";
    private final String COMMON_END = "_**";
    
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(RepositoryNotification.class);
    
    
    public RepositoryNotification(SessionStore sessions, ScmWikiProvider provider) {
        this.sessions = sessions;
        this.provider = provider;
    }
    
    public void notifyForRepository(String repositoryToUpdate){
        this.repositoryToUpdate = repositoryToUpdate;
        updatedDirectories.clear();
        updateRepository();
    }
    
    private void updateRepository() {
        Iterator<HttpSession> sessionIter = sessions.getAll();
        while (sessionIter.hasNext()) {
            HttpSession s = sessionIter.next();
            synchronized (s) {
                updateWikisInSession(s);
            }
        }
    }

    private void updateWikisInSession(HttpSession session) {
        try {
            Enumeration<String> attributes = session.getAttributeNames();
            if (attributes != null) {
                Account account = (Account) session.getAttribute("com.cloudogu.wiki.Account");
                while (attributes.hasMoreElements()) {
                    String attribute = attributes.nextElement();
                    tryToUpdateWikiForAttribute(account, attribute);
                }
            }
        } catch (IllegalStateException ex) {
            LOG.debug("Tried to get attributes from invalid session.");
        }
    }

    private void tryToUpdateWikiForAttribute(Account account, String attribute) {
        if (attribute.startsWith(COMMON_START)) {
            String wikiNameWithLanguage = attribute.substring(COMMON_START.length());
            String wikiName = wikiNameWithLanguage.substring(0, wikiNameWithLanguage.length() - COMMON_END.length());
            if (wikiRequiresUpdate(wikiName)) {
                updateWiki(wikiName, account);
            }
        }
    }
    
    private boolean wikiRequiresUpdate(String wikiName){
        return wikiName.startsWith(repositoryToUpdate) && !updatedDirectories.contains(wikiName);
    }

    private void updateWiki(String wikiName, Account account) {
        LOG.info("found repository {} for user {}", wikiName, account.getUsername());
        File dir = provider.getRepositoryDirectory(wikiName);
        String branch = ScmWikiProvider.getDecodedBranchName(wikiName);
        try {
            LOG.info("pull changes");
            provider.pullChanges(account, dir, branch);
            updatedDirectories.add(wikiName);
        } catch (GitAPIException | IOException ex) {
            LOG.warn(null, ex);
        }
    }
    
}
