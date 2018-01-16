package com.cloudogu.wiki;

/**
 * @author Maren Süwer
 */
public class ScmConnectionException extends RuntimeException
{
    // Konstruktor unserer eigenen Exception
    public ScmConnectionException(String exceptionMessage, Exception ex)
    {
        super(exceptionMessage, ex);
    }
}