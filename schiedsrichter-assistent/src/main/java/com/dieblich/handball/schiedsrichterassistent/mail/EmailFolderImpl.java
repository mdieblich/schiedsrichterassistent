package com.dieblich.handball.schiedsrichterassistent.mail;

import jakarta.mail.*;
import lombok.Getter;

import java.util.*;

public class EmailFolderImpl implements EmailFolder {
    private final Folder folder;
    private final Session session;

    private final List<Email> fetchedEmails = new ArrayList<>();
    @Getter
    private final Map<Message, Exception> failedEmails = new HashMap<>();

    public EmailFolderImpl(Folder folder, Session session) throws EmailException {
        this.folder = folder;
        this.session = session;
        fetchEmails();
    }
    private void fetchEmails() throws EmailException {
        try {
            for(Message message: folder.getMessages()){
                try{
                    fetchedEmails.add(new Email(message));
                } catch (EmailException e) {
                    failedEmails.put(message, e);
                }
            }
        } catch (MessagingException e) {
            throw new EmailException("Fehler beim Abrufen der Emails aus " + folder, e);
        }
    }

    @Override
    public List<Email> getEmails(){
        return fetchedEmails;
    }


    @Override
    public void upload(Email email) throws EmailException {
        try{
            Message[] messageArray = new Message[]{email.getJakartaMessage(session)};
            folder.appendMessages(messageArray);
        } catch (Exception e) {
            throw new EmailException("Fehler beim Hochladen der Email " + email, e);
        }
    }

    @Override
    public void deleteAll() throws EmailException {
        try {
            for (Message message : folder.getMessages()) {
                message.setFlag(Flags.Flag.DELETED, true);
            }
            folder.close();
            // TODO: Really delete
            // folder.expunge();
        }catch(MessagingException e){
            throw new EmailException("Dateien konnten nicht gelöscht werden.", e);
        }
    }
}
