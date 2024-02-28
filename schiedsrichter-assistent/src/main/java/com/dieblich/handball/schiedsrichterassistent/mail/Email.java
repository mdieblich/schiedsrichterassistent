package com.dieblich.handball.schiedsrichterassistent.mail;

import jakarta.mail.*;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.io.IOException;
import java.util.Optional;

public class Email {

    private final Message message;

    public Email(Message message){
        this.message = message;
    }

    public Email(Session session) {
        this.message = new MimeMessage(session);
    }

    Message getJakartaMessage() {
        return message;
    }

    public boolean isFrom(String sender) throws MessagingException {
        for (Address from: message.getFrom()) {
            if(from instanceof InternetAddress){
                String fromString = ((InternetAddress)from).getAddress();
                if(sender.equals(fromString)){
                    return true;
                }
            }
        }
        return false;
    }

    public String getContent() throws MessagingException, IOException {
        return message.getContent().toString();
    }

    public void deleteImmediately() throws MessagingException {
       message.setFlag(Flags.Flag.DELETED, true);
       message.getFolder().expunge();
    }

    public void setSubject(String subject) throws MessagingException {
        message.setSubject(subject);
    }

    public void setFrom(String emailAddress) throws MessagingException {
        message.setFrom(new InternetAddress(emailAddress));
    }

    public void setContent(String content) throws MessagingException {
        message.setText(content);
    }
}
