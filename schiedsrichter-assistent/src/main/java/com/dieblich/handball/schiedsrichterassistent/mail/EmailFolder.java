package com.dieblich.handball.schiedsrichterassistent.mail;

import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EmailFolder {
    private final Folder folder;

    public EmailFolder(Folder folder){
        this.folder = folder;
    }

    public List<Email> getEmails() throws MessagingException {
        return Arrays.stream(folder.getMessages())
                .map(Email::new)
                .collect(Collectors.toList());
    }

    public void upload(Email email) throws MessagingException {
        folder.appendMessages(new Message[]{email.getJakartaMessage()});
    }
}
