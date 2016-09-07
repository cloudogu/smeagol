/**
 * Copyright (c) 2016 Cloudogu GmbH. All Rights Reserved.
 * 
 * Copyright notice
 */

package com.cloudogu.wiki.scmm;

import com.github.sdorra.milieu.Configuration;

/**
 *
 * @author Sebastian Sdorra
 */
public class ScmConfiguration {

    @Configuration("SCM_INSTANCE_URL")
    private String instanceUrl;

    public String getInstanceUrl() {
        return instanceUrl;
    }
    
}
