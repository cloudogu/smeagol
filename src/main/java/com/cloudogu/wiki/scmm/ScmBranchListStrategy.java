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
 * @author Michael Behlendorf
 */
public abstract class ScmBranchListStrategy {

    private static final Logger LOG = LoggerFactory.getLogger(ScmWikiListStrategy.class);

    private final ScmConfiguration scmConfiguration;

    protected ScmBranchListStrategy(ScmConfiguration scmConfiguration) {
        this.scmConfiguration = scmConfiguration;
    }

    public abstract List<Wiki> getWikis(String repository);

    protected List<Wiki> fetchWikis(WikiContext context, String repository) {
        LOG.info("fetch wiki repositories from scm-manager");

        Account account = context.getAccount();
        return ScmManager.getBranches(account, scmConfiguration.getInstanceUrl(), repository);
    }

}
