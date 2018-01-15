package com.cloudogu.wiki;

/**
 * @author Maren Süwer
 */
public class NoSCMConnectionException extends RuntimeException
{
    // Konstruktor unserer eigenen Exception
    public NoSCMConnectionException()
    {
        super("No Connection to scm manager possible!");
    }
}