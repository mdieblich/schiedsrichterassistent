package com.dieblich.handball.schiedsrichterassistent.mail;

import jakarta.mail.MessagingException;

public interface EmailServerRead {
    EmailFolder getFolder(String name) throws MessagingException;
}
