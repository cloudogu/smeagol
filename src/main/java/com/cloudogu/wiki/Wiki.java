/**
 * Copyright (c) 2016 Cloudogu GmbH. All Rights Reserved.
 * 
 * Copyright notice
 */

package com.cloudogu.wiki;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Wiki describes a single wiki instance in the application context.
 * 
 * @author Sebastian Sdorra
 */
public class Wiki {

    private final String SEPARATOR = "/";

    private final String repositoryId;
    private final String branchName;
    private final String displayName;
    private final String description;

    /**
     * Constructs a new Wiki.
     *
     * @param repositoryId identifier of the repository of the instance
     * @param branchName name of the branch of the instance
     * @param displayName display name which is used at the overview page
     * @param description short description of the wiki
     */
    public Wiki(String repositoryId, String branchName, String displayName, String description) {
        this.repositoryId = repositoryId;
        this.branchName = branchName;
        this.displayName = displayName;
        this.description = description;
    }

    public String getName() {
        return this.repositoryId + SEPARATOR + this.branchName + SEPARATOR;
    }

    public String getUrlEncodedName() {
        try {
            return this.repositoryId + SEPARATOR + URLEncoder.encode(branchName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return this.getName(); //TODO: what to do?
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getDescription() {
        return description;
    }

    public String getRepositoryId() {
        return repositoryId;
    }

    public String getBranchName() {
        return branchName;
    }

}
