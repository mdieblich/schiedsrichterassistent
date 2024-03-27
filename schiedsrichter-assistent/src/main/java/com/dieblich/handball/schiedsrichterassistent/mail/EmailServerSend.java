package com.dieblich.handball.schiedsrichterassistent.mail;

import com.dieblich.handball.schiedsrichterassistent.SchiriConfiguration;
import com.dieblich.handball.schiedsrichterassistent.mail.templates.AskForConfigurationEmail;
import com.dieblich.handball.schiedsrichterassistent.mail.templates.ConfigConfirmationEmail;
import com.dieblich.handball.schiedsrichterassistent.mail.templates.DontKnowWhatToDoEmail;
import com.dieblich.handball.schiedsrichterassistent.mail.templates.WelcomeEmail;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.mail.*;

import java.util.List;
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

    public ConfigConfirmationEmail createConfigConfirmationEmail(String schiriEmailAddress, SchiriConfiguration config, List<String> log) throws MessagingException, JsonProcessingException {
        return new ConfigConfirmationEmail(botEmailAddress, schiriEmailAddress, config, log, session);
    }

    public AskForConfigurationEmail createAskForConfigEmail(String schiriEmailAddress) throws MessagingException {
        return new AskForConfigurationEmail(botEmailAddress, schiriEmailAddress, session);
    }

    public DontKnowWhatToDoEmail createResponseForUnknownEmail(String schiriEmailAddress, Email unknownEmail) throws MessagingException {
        return new DontKnowWhatToDoEmail(botEmailAddress, schiriEmailAddress, unknownEmail, session);
    }
}
