package com.dieblich.handball.schiedsrichterassistent.mail;

import com.dieblich.handball.schiedsrichterassistent.MissingConfigException;
import com.dieblich.handball.schiedsrichterassistent.Schiedsrichter;
import com.dieblich.handball.schiedsrichterassistent.SchiriConfiguration;
import com.dieblich.handball.schiedsrichterassistent.calendar.SpielTermin;
import com.dieblich.handball.schiedsrichterassistent.geo.GeoException;
import com.dieblich.handball.schiedsrichterassistent.mail.templates.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.mail.*;

import java.io.IOException;
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

    public CalendarResponseEmail createCalendarResponse(String schiriEmailAddress, SpielTermin spielTermin) throws MessagingException, GeoException, MissingConfigException, IOException {
        return new CalendarResponseEmail(botEmailAddress, schiriEmailAddress, spielTermin, session);
    }

    public SecondSchiriMissingEmail createSecondSchiriMissingEmail(String schiriEmailAddress, Schiedsrichter otherSchiri) throws MessagingException {
        return new SecondSchiriMissingEmail(botEmailAddress, schiriEmailAddress, otherSchiri, session);
    }
}
