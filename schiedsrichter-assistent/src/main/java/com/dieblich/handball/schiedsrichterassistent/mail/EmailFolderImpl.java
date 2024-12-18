package com.dieblich.handball.schiedsrichterassistent.mail;

import jakarta.mail.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class EmailFolderImpl implements EmailFolder {
    private static final Logger logger = LoggerFactory.getLogger(EmailFolderImpl.class);

    private final Folder folder;
    private final Session session;

    private List<Email> fetchedEmails;

    public EmailFolderImpl(Folder folder, Session session) {
        this.folder = folder;
        this.session = session;
    }
    private void fetchEmails() throws EmailException {
        try {
            logger.info("Fetching emails");
            fetchedEmails = new ArrayList<>();
            Map<Message, Exception> failedEmails = new HashMap<>();
            for(Message message: folder.getMessages()){
                try{
                    fetchedEmails.add(new Email(message));
                } catch (EmailException e) {
                    failedEmails.put(message, e);
                }
            }
            logger.info("Emails fetched");
            for(Map.Entry<Message, Exception> failedEmail: failedEmails.entrySet()){
                int messageNumber = failedEmail.getKey().getMessageNumber();
                Exception exception  = failedEmail.getValue();
                logger.warn("Exception thrown for message #" + messageNumber, exception);
            }
        } catch (MessagingException e) {
            throw new EmailException("Fehler beim Abrufen der Emails aus " + folder, e);
        }
    }

    @Override
    public List<Email> getEmails() throws EmailException {
        if(fetchedEmails == null){
            fetchEmails();
        }
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
