package com.dieblich.handball.schiedsrichterassistent.mail;

public class EmailException extends Exception{
    public EmailException(String message, Throwable cause) {
        super(message, cause);
    }

    public EmailException(String message) {
        super(message);
    }
}
