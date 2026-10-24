package org.ambrosia.notification_service.exception;

public class UserDoesntFollowedException extends RuntimeException{
    public UserDoesntFollowedException(){
        super("User doesnt followed on community!");
    }
}
