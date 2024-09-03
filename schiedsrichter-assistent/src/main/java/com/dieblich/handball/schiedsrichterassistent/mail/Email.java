package com.dieblich.handball.schiedsrichterassistent.mail;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;

@ToString
public class Email {
    private Message jakartaMessage;

    @Getter
    private final String sender;

    @Getter
    private final LocalDateTime sentDate;

    @Getter
    private final List<String> receivers;

    @Getter
    private final String subject;

    @Getter
    private final String content;

    private final List<File> attachments;

    public Email(Message message) throws EmailException {
        try {
            jakartaMessage = message;
            subject = message.getSubject();
            sender = extractSender(message);
            this.sentDate =  LocalDateTime.ofInstant(message.getSentDate().toInstant(), ZoneId.systemDefault());
            content = extractContent(message);
            receivers = extractReceivers(message);
            attachments = List.of();
        } catch (Exception e) {
            throw new EmailException("Error reading content of message: " + message, e);
        }
    }

    public Email(String sender, String receiver, String subject, String content, File... attachments){
        this.sender = sender;
        this.sentDate = LocalDateTime.now();
        this.receivers = List.of(receiver);
        this.subject = subject;
        this.content = content;
        this.attachments = List.of(attachments);
    }

    private @NotNull String extractSender(Message message) throws MessagingException, EmailException {
        return Arrays.stream(message.getFrom())
                .filter(address -> address instanceof InternetAddress)
                .map(address -> (InternetAddress) address)
                .map(InternetAddress::getAddress)
                .findFirst().orElseThrow(() -> new EmailException("Absender konnte nicht bestimmt werden: " + message));
    }

    private String extractContent(Message message) throws MessagingException, IOException {
        if (message.isMimeType("text/plain")) {
            return message.getContent().toString();
        }
        if(message.isMimeType("text/html")){
            return Jsoup.parse(message.getContent().toString()).text();
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

    private List<String> extractReceivers(Message message) throws MessagingException {
        return Arrays.stream(message.getRecipients(Message.RecipientType.TO))
                .filter(address -> address instanceof InternetAddress)
                .map(address -> (InternetAddress) address)
                .map(InternetAddress::getAddress)
                .toList();
    }

    public boolean isFrom(String sender){
        return this.sender.equals(sender);
    }
    public boolean hasSubject(String emailSubject) {
        return subject.equals(emailSubject);
    }

    public void deleteImmediately() throws MessagingException {
        jakartaMessage.setFlag(Flags.Flag.DELETED, true);
        jakartaMessage.getFolder().expunge();
    }

    public Message getJakartaMessage(Session emailSession) throws EmailException {
        if(jakartaMessage == null){
            try {
                jakartaMessage = createJakartaMessage(emailSession);
            } catch (Exception e) {
                throw new EmailException("Konnte die Jakarta-Email nicht erstellen, " + this, e);
            }
        }
        return jakartaMessage;
    }

    private MimeMessage createJakartaMessage(Session emailSession) throws MessagingException, IOException {
        MimeMessage message = new MimeMessage(emailSession);
        message.setFrom(new InternetAddress(sender));
        message.setRecipients(Message.RecipientType.TO,createReceiverArray());
        message.setSubject(subject);
        message.setContent(createContent());
        return message;
    }

    private InternetAddress[] createReceiverArray() throws AddressException {
        InternetAddress[] receiverAddresses = new InternetAddress[receivers.size()];
        for(int i=0; i< receivers.size(); i++) {
            receiverAddresses[i] = new InternetAddress(receivers.get(i));
        }
        return receiverAddresses;
    }

    private Multipart createContent() throws MessagingException, IOException {
        MimeMultipart multiPart = new MimeMultipart();

        multiPart.addBodyPart(createTextContent());
        for(File attachment:attachments){
            multiPart.addBodyPart(createFileAttachmentContent(attachment));
        }

        return multiPart;
    }

    private BodyPart createTextContent() throws MessagingException {
        MimeBodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setText(content);
        return messageBodyPart;
    }

    private BodyPart createFileAttachmentContent(File attachment) throws MessagingException, IOException {
        MimeBodyPart attachmentPart = new MimeBodyPart();
        attachmentPart.attachFile(attachment);
        return attachmentPart;
    }

    protected List<File> getAttachments() {
        return attachments;
    }
}
