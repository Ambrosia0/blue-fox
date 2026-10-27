package org.ambrosia.notification_service.exception;

public class PostDoesntExistException extends RuntimeException {
    public PostDoesntExistException() {
        super("Post doesn't exist!");
    }
}
