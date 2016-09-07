/**
 * Copyright (c) 2016 Cloudogu GmbH. All Rights Reserved.
 * 
 * Copyright notice
 */

package com.cloudogu.wiki;

/**
 * Wiki describes a single wiki instance in the application context.
 * 
 * @author Sebastian Sdorra
 */
public class Wiki {

    private final String name;
    private final String displayName;
    private final String description;

    /**
     * Constructs a new Wiki.
     * 
     * @param name identifier of the instance
     * @param displayName display name which is used at the overview page
     * @param description short description of the wiki
     */
    public Wiki(String name, String displayName, String description) {
        this.name = name;
        this.displayName = displayName;
        this.description = description;
    }

    public String getName() {
        return name;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getDescription() {
        return description;
    }
    
}
