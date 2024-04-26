package com.dieblich.handball.schiedsrichterassistent.mail;

import java.util.List;

public interface EmailFolder {
    List<Email> getEmails();

    void upload(Email email) throws EmailException;

    void deleteAll() throws EmailException;
}
