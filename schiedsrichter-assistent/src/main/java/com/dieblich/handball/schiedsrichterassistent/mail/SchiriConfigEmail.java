package com.dieblich.handball.schiedsrichterassistent.mail;

import com.dieblich.handball.schiedsrichterassistent.SchiriConfiguration;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SchiriConfigEmail{

    // Only needed to create the Email later;
    private Session session;
    private Email email;
    private SchiriConfiguration config;

    public SchiriConfigEmail(SchiriConfiguration config, Session session){
        this.config = config;
        this.session = session;
    }

    public SchiriConfigEmail(Email email) {
        this.email = email;
    }

    public Email getEmail() throws MessagingException, JsonProcessingException {
        if(email == null){
            email = new Email(session);
            email.setSubject("Konfiguration vom " + now());
            email.setFrom(config.Benutzerdaten.Email);
            email.setContent(config.toJSON());
        }
        return email;
    }

    public SchiriConfiguration getConfig() throws MessagingException, IOException {
        if(config == null){
            config = SchiriConfiguration.fromJSON(email.getContent());
        }
        return config;
    }

    private static String now(){
        Date now = new Date();
        return new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(now);
    }
}
