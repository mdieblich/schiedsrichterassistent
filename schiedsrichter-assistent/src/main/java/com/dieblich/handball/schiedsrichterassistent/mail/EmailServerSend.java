package com.dieblich.handball.schiedsrichterassistent.mail;

import com.dieblich.handball.schiedsrichterassistent.mail.templates.WelcomeEmail;
import jakarta.mail.*;

import java.util.Properties;

public class EmailServerSend {

    private final Session session;
    private final String botEmailAddress;

    public EmailServerSend(String host, int port, String botEmailAddress, String password) {
        this.botEmailAddress = botEmailAddress;
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

    public WelcomeEmail createWelcomeEmail(String schiriEmailAddress) throws MessagingException {
        return new WelcomeEmail(botEmailAddress, schiriEmailAddress, session);
    }
}
