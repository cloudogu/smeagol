/**
 * Copyright (c) 2016 Cloudogu GmbH. All Rights Reserved.
 * 
 * Copyright notice
 */

package com.cloudogu.wiki;

/**
 * Generic wiki exeception.
 * 
 * @author Sebastian Sdorra
 */
public class WikiException extends RuntimeException {

    private static final long serialVersionUID = 4159665239556435093L;

    public WikiException(String message) {
        super(message);
    }

    public WikiException(String message, Throwable cause) {
        super(message, cause);
    }

    public WikiException(Throwable cause) {
        super(cause);
    }

    public WikiException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
