/**
 * Copyright (c) 2016 Cloudogu GmbH. All Rights Reserved.
 * 
 * Copyright notice
 */

package com.cloudogu.wiki;

/**
 * This exception is thrown if an error occurs during authentication.
 * 
 * @author Sebastian Sdorra
 */
public class WikiAuthenticationException extends WikiException {

    private static final long serialVersionUID = 3719105356407857013L;

    public WikiAuthenticationException(String message) {
        super(message);
    }

    public WikiAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }

}
