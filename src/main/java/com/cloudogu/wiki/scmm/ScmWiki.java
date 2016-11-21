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

    public ScmWiki(String repositoryId, String branchName, String displayName, String description, String remoteUrl) {
        super(repositoryId, branchName, displayName, description);
        this.remoteUrl = remoteUrl;
    }

    public String getRemoteUrl() {
        return remoteUrl;
    }

    public String getRepository() {
        return super.getRepositoryId();
    }

    public String getBranch() {
        return super.getBranchName();
    }

}
