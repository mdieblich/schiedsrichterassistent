package com.dieblich.handball.schiedsrichterassistent.mail;

import jakarta.mail.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EmailFolderImpl implements EmailFolder {
    private final Folder folder;
    private final Session session;

    public EmailFolderImpl(Folder folder, Session session){
        this.folder = folder;
        this.session = session;
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

    @Override
    public Email prepareEmailForUpload() throws MessagingException {
        return new Email(session);
    }
}
