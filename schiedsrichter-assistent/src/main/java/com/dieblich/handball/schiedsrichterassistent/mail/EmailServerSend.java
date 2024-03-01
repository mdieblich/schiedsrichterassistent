package com.dieblich.handball.schiedsrichterassistent.mail;

import jakarta.mail.*;

import java.util.Properties;

public class EmailServerSend {

    private final Session session;
    private final String host;
    private final int port;
    private final String user;
    private final String password;

    public EmailServerSend(String host, int port, String user, String password) {
        this.host = host;
        this.port = port;
        this.user = user;
        this.password = password;
        Properties props = System.getProperties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        Authenticator authenticator = new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, password);
            }
        };
        session = Session.getInstance(props, authenticator);
    }

    public WelcomeEmail createWelcomeEmail(String receiver) throws MessagingException {
        return new WelcomeEmail(user, receiver, session);
    }
}
