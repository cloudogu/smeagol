/**
 * Copyright (c) 2016 Cloudogu GmbH. All Rights Reserved.
 * 
 * Copyright notice
 */

package com.cloudogu.wiki.scmm;

import com.cloudogu.wiki.Account;
import com.cloudogu.wiki.Wiki;
import com.cloudogu.wiki.WikiContext;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Sebastian Sdorra
 */
public abstract class ScmWikiListStrategy {

    private static final Logger LOG = LoggerFactory.getLogger(ScmWikiListStrategy.class);
        
    private final ScmConfiguration scmConfiguration;

    protected ScmWikiListStrategy(ScmConfiguration scmConfiguration) {
        this.scmConfiguration = scmConfiguration;
    }
    
    public abstract List<Wiki> getWikis();
    
    protected List<Wiki> fetchWikis(WikiContext context) {
        LOG.info("fetch wiki repositories from scm-manager");

        Account account = context.getAccount();
        return ScmManager.getPotentialWikis(account, scmConfiguration.getInstanceUrl());
    }
    
}
