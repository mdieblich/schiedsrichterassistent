package com.dieblich.handball.schiedsrichterassistent.mail;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Email {

    private final Message message;

    public Email(Message message){
        this.message = message;
    }

    public Email(Session session) {
        this.message = new MimeMessage(session);
    }
    public Email(String botEmailAddress, String schiriEmailAddress, Session session) throws MessagingException {
        this.message = new MimeMessage(session);
        setFrom(botEmailAddress);
        setTo(schiriEmailAddress);
    }

    Message getJakartaMessage() {
        return message;
    }

    public boolean isFrom(String sender) throws MessagingException {
        return getAllSenders().stream()
                .anyMatch(sender::equals);
    }

    private List<String> getAllSenders() throws MessagingException {
        return Arrays.stream(message.getFrom())
                .filter(address -> address instanceof InternetAddress)
                .map(address -> (InternetAddress) address)
                .map(InternetAddress::getAddress)
                .collect(Collectors.toList());
    }

    public String getContent() throws MessagingException, IOException {
        if (message.isMimeType("text/plain")) {
            return message.getContent().toString();
        }
        if (message.isMimeType("multipart/*")) {
            MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
            return getTextFromMimeMultipart(mimeMultipart);
        }
        return "";
    }

    private String getTextFromMimeMultipart(MimeMultipart mimeMultipart)  throws MessagingException, IOException{
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < mimeMultipart.getCount(); i++) {
            BodyPart bodyPart = mimeMultipart.getBodyPart(i);
            if (bodyPart.isMimeType("text/plain")) {
                return result + "\n" + bodyPart.getContent(); // without return, same text appears twice in my tests
            }
            result.append(this.parseBodyPart(bodyPart));
        }
        return result.toString();
    }

    private String parseBodyPart(BodyPart bodyPart) throws MessagingException, IOException {
        if (bodyPart.isMimeType("text/html")) {
            return "\n" + org.jsoup.Jsoup
                    .parse(bodyPart.getContent().toString())
                    .text();
        }
        if (bodyPart.getContent() instanceof MimeMultipart){
            return getTextFromMimeMultipart((MimeMultipart)bodyPart.getContent());
        }

        return "";
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

    public void setTo(String receiver) throws MessagingException {
        message.setRecipients(
                Message.RecipientType.TO,
                InternetAddress.parse(receiver)
        );
    }

    public void setContent(String content) throws MessagingException {
        message.setText(content);
    }

    public Optional<String> getFrom() throws MessagingException {
        return getAllSenders().stream().findAny();
    }
    public String getSubject() throws MessagingException {
        return message.getSubject();
    }

    public void send() throws MessagingException {
        Transport.send(message);
    }

}
