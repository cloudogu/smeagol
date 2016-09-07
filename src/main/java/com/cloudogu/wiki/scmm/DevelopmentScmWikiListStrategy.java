/**
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
 * @author Sebastian Sdorra
 */
public class DevelopmentScmWikiListStrategy extends ScmWikiListStrategy {

    public DevelopmentScmWikiListStrategy(ScmConfiguration scmConfiguration) {
        super(scmConfiguration);
    }

    @Override
    public List<Wiki> getWikis() {
        return fetchWikis(WikiContextFactory.getInstance().get());
    }

}
