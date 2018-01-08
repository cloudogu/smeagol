/**
 * Copyright (c) 2016 Cloudogu GmbH. All Rights Reserved.
 * 
 * Copyright notice
 */

package com.cloudogu.wiki;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Wiki describes a single wiki instance in the application context.
 * 
 * @author Sebastian Sdorra
 */
public class Wiki {
    
    private static final Logger LOG = LoggerFactory.getLogger(Wiki.class);

    private final static String SEPARATOR = "/";
    private final static String FILE_NAME_SEPARATOR = "/";

    private final String repositoryId;
    private final String branchName;
    private final String displayName;
    private final String description;
    private final String revision;
    private boolean group;
    private String groupName;

    /**
     * Constructs a new Wiki.
     *
     * @param repositoryId identifier of the repository of the instance
     * @param branchName name of the branch of the instance
     * @param displayName display name which is used at the overview page
     * @param description short description of the wiki
     * @param revision revision of branch
     */
    public Wiki(String repositoryId, String branchName, String displayName, String description, String revision) {
        this.repositoryId = repositoryId;
        this.branchName = branchName;
        this.description = description;
        this.revision = revision;

        //parseName(displayName);
        String splitedName[] = displayName.split(FILE_NAME_SEPARATOR, 2);
        this.displayName = splitedName[splitedName.length-1];
        if(splitedName.length == 1){
            this.group = false;
            this.groupName = "main";
        }
        else {
            this.groupName = splitedName[0];
            this.group = true;
        }
    }

    public String getName() {
        return this.repositoryId + SEPARATOR + this.branchName;
    }

    public String getUrlEncodedName() {
        try {
            return this.repositoryId + SEPARATOR + URLEncoder.encode(branchName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            LOG.warn("failed to encode wiki name", e);
        }
        return this.getName();
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

    public String getRevision() {
        return revision;
    }

    public Boolean hasGroup() { return group; }

    public String getGroupName() { return groupName; }
}
