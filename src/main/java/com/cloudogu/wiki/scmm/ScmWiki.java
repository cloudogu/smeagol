/**
 * Copyright (c) 2016 Cloudogu GmbH. All Rights Reserved.
 * 
 * Copyright notice
 */

package com.cloudogu.wiki.scmm;

import com.cloudogu.wiki.Wiki;

/**
 *
 * @author Sebastian Sdorra
 */
public class ScmWiki extends Wiki {

    private final String remoteUrl;
    
    public ScmWiki(String name, String displayName, String description, String remoteUrl) {
        super(name, displayName, description);
        this.remoteUrl = remoteUrl;
    }

    public String getRemoteUrl() {
        return remoteUrl;
    }
    
}
