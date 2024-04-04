package com.dieblich.handball.schiedsrichterassistent.mail;

import jakarta.mail.Flags;
import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EmailFolderSMTP implements EmailFolder {
    private final Folder folder;

    public EmailFolderSMTP(Folder folder){
        this.folder = folder;
    }

    @Override
    public List<Email> getEmails() throws MessagingException {
        return Arrays.stream(folder.getMessages())
                .map(Email::new)
                .collect(Collectors.toList());
    }

    @Override
    public void upload(Email email) throws MessagingException {
        folder.appendMessages(new Message[]{email.getJakartaMessage()});
    }

    @Override
    public void deleteAll() throws MessagingException {
        for (Message message:folder.getMessages()) {
            message.setFlag(Flags.Flag.DELETED, true);
        }
        folder.close();
        // Later: Really delete
        // folder.expunge();
    }
}
