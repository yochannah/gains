package com.digitalcranberry.gainsl.exception;

/**
 * Created by yo on 14/06/15.
 */
public class ServerUnavailableException extends RuntimeException {
    public ServerUnavailableException() {
        super();
    }

    public ServerUnavailableException(String message) {
        super(message);
    }
}
