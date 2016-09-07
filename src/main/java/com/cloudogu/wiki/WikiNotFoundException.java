/**
 * Copyright (c) 2016 Cloudogu GmbH. All Rights Reserved.
 * 
 * Copyright notice
 */

package com.cloudogu.wiki;

/**
 * This exception is thrown if the requested wiki could not be found.
 * 
 * @author Sebastian Sdorra
 */
public class WikiNotFoundException extends WikiException {

    private static final long serialVersionUID = 2453392435099970261L;

    public WikiNotFoundException(String message) {
        super(message);
    }

}
