package com.dieblich.handball.schiedsrichterassistent.mail;

import jakarta.mail.MessagingException;

import java.util.List;

public interface EmailFolder {
    List<Email> getEmails() throws MessagingException;

    void upload(Email email) throws MessagingException;

    void deleteAll() throws MessagingException;
}
