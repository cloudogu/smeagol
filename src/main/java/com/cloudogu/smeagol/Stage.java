/**
 * Copyright (c) 2016 Cloudogu GmbH. All Rights Reserved.
 * 
 * Copyright notice
 */

package com.cloudogu.smeagol;

import com.google.common.base.Strings;

import java.util.Locale;

/**
 * Application Stage. The stage determines if the application is trimmed for performance and security or more for fast 
 * development cycles.
 * 
 * @author Sebastian Sdorra
 */
public enum Stage {

    /**
     * Application is trimmed for security and performance. This stage should be used for production deployments. The
     * production stage is the default stage.
     */
    PRODUCTION, 
    
    /**
     * Application is trimmed for faster development cycles and auto reload. In the development stage the most caches
     * and security checks are disabled. <strong>Warning:</strong> This stage should never be used in production.
     */
    DEVELOPMENT;


    /**
     * Returns the stage from string representation.
     * @param name string representation.
     *
     * @return stage
     */
    public static Stage fromString(String name) {
        if (Strings.isNullOrEmpty(name)) {
            return Stage.PRODUCTION;
        }
        return Stage.valueOf(name.toUpperCase(Locale.ENGLISH));
    }
}
