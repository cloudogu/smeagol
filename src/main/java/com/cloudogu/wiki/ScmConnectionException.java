package com.cloudogu.wiki;

/**
 * This exception is thrown if the scm manager cannot be reached.
 *
 * @author Maren Süwer
 */
public class ScmConnectionException extends RuntimeException
{
    public ScmConnectionException(String message, Throwable ex)
    {
        super(message, ex);
    }
}