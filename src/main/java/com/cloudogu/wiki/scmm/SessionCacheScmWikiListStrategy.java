/**
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
 * @author Sebastian Sdorra
 */
public class SessionCacheScmWikiListStrategy extends ScmWikiListStrategy {   

    public SessionCacheScmWikiListStrategy(ScmConfiguration scmConfiguration) {
        super(scmConfiguration);
    }
    
    @Override
    public List<Wiki> getWikis() {
        WikiContext context = WikiContextFactory.getInstance().get();
        final HttpSession session = context.getRequest().getSession(true);

        List<Wiki> wikis;
        synchronized (session) {
            wikis = (List<Wiki>) session.getAttribute(ScmWikiProvider.class.getName());
            if (wikis == null) {
                wikis = fetchWikis(context);
                session.setAttribute(ScmWikiProvider.class.getName(), wikis);
            }
        }

        return wikis;
    }

}
