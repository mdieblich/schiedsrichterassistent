package com.dieblich.handball.schiedsrichterassistent.mail;

public interface EmailServerRead {
    EmailFolder fetchFolder(String name) throws EmailException;
}
