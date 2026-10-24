package org.ambrosia.notification_service.exception;

public class UserBannedException extends RuntimeException{
    public UserBannedException(){
        super("User is banned in community!");
    }
}
