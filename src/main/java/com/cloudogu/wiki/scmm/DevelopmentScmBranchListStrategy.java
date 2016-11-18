/*
 * Copyright (c) 2016 Cloudogu GmbH. All Rights Reserved.
 * 
 * Copyright notice
 */
package com.cloudogu.wiki.scmm;

import com.cloudogu.wiki.Wiki;
import com.cloudogu.wiki.WikiContextFactory;
import java.util.List;

/**
 *
 * @author mbehlendorf
 */
public class DevelopmentScmBranchListStrategy extends ScmBranchListStrategy {

    public DevelopmentScmBranchListStrategy(ScmConfiguration scmConfiguration) {
        super(scmConfiguration);
    }

    @Override
    public List<Wiki> getWikis(String repository) {
        return fetchWikis(WikiContextFactory.getInstance().get(), repository);
    }

}
