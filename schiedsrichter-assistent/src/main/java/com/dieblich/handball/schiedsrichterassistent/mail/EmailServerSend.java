package com.dieblich.handball.schiedsrichterassistent.mail;

import jakarta.mail.*;

import java.util.Properties;

public class EmailServerSend {

    private final Session session;

    public EmailServerSend(String host, int port, String botEmailAddress, String password) {
        Properties props = System.getProperties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        Authenticator authenticator = new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(botEmailAddress, password);
            }
        };
        session = Session.getInstance(props, authenticator);
    }

    public void send(Email email) throws EmailException {
        try{
            Transport.send(email.getJakartaMessage(session));
        } catch(Exception e){
            throw new EmailException("Error while sending email: " + this, e);
        }
    }
}
