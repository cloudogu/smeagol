package com.cloudogu.smeagol;

/**
 * This exception is thrown if an error occurs during authentication.
 *
 * @author Sebastian Sdorra
 */
public class AuthenticationException extends RuntimeException {

    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
