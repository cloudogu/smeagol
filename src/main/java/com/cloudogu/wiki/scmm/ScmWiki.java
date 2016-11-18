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
    private String repository;
    private String branch;

    public ScmWiki(String name, String displayName, String description, String remoteUrl) {
        super(name, displayName, description);
        this.remoteUrl = remoteUrl;
        int index = name.indexOf('/');
        if (index > 0) {
            this.repository = name.substring(0, index);
            this.branch = name.substring(index + 1);
        }
    }

    public String getRemoteUrl() {
        return remoteUrl;
    }

    public String getRepository() {
        return repository;
    }

    public String getBranch() {
        return branch;
    }

}
