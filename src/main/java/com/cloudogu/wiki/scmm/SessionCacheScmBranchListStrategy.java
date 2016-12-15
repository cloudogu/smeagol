/*
 * Copyright (c) 2016 Cloudogu GmbH. All Rights Reserved.
 * 
 * Copyright notice
 */
package com.cloudogu.wiki.scmm;

import com.cloudogu.wiki.Wiki;
import com.cloudogu.wiki.WikiContext;
import com.cloudogu.wiki.WikiContextFactory;
import java.util.List;
import javax.servlet.http.HttpSession;

/**
 *
 * @author mbehlendorf
 */
public class SessionCacheScmBranchListStrategy extends ScmBranchListStrategy {

    public SessionCacheScmBranchListStrategy(ScmConfiguration scmConfiguration) {
        super(scmConfiguration);
    }

    @Override
    public List<Wiki> getWikis(String repository) {
        WikiContext context = WikiContextFactory.getInstance().get();
        final HttpSession session = context.getRequest().getSession(true);

        List<Wiki> wikis;
        synchronized (session) {
            String cacheKey = cacheKey(repository);
            wikis = (List<Wiki>) session.getAttribute(cacheKey);
            if (wikis == null) {
                wikis = fetchWikis(context, repository);
                session.setAttribute(cacheKey, wikis);
            }
        }

        return wikis;
    }

    private String cacheKey(String repository) {
        return SessionCacheScmBranchListStrategy.class.getName().concat(":").concat(repository);
    }
}
