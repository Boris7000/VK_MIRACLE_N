package com.vkontakte.miracle.throwable.users;

public class InvalidCurrentUserException extends IllegalStateException{

    public InvalidCurrentUserException() {
    }

    public InvalidCurrentUserException(String s) {
        super(s);
    }
}
