package com.cloudogu.wiki;

/**
 * @author Maren Süwer
 */
public class ScmConnectionException extends RuntimeException
{
    // Konstruktor unserer eigenen Exception
    public ScmConnectionException()
    {
        super("No Connection to scm manager possible!");
    }
}