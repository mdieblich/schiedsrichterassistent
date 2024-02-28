package com.dieblich.handball.schiedsrichterassistent.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.Session;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

public class UserConfiguration{
    private final String userEmail;
    private final Properties configuration = new Properties();

    public UserConfiguration(String userEmail, String content) throws IOException {
        this(userEmail);
        configuration.load(new StringReader(content));
    }

    public static UserConfiguration DEFAULT(String userEmail){
        UserConfiguration config = new UserConfiguration(userEmail);
        config.set("Umziehen.DauerInMinuten", "15");
        config.set("TechnischeBesprechung.Oberliga.DauerInMinuten", "45");
        config.set("TechnischeBesprechung.UnterOberliga.DauerInMinuten", "30");
        return config;
    }

    private UserConfiguration(String userEmail){
        this.userEmail = userEmail;
    }

    public void set(String key, String value) {
        configuration.put(key, value);
    }

    public Email toEmail(Session session) throws MessagingException {
        Email email = new Email(session);
        email.setSubject("Konfiguration vom " + now());
        email.setFrom(userEmail);
        email.setContent(configToString());
        return email;
    }

    private String now(){
        Date now = new Date();
        return new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(now);
    }

    private String configToString() {
        try {
            StringWriter writer = new StringWriter();
            configuration.store(new PrintWriter(writer), null);
            return writer.getBuffer().toString();
        } catch (IOException e) {
            throw new RuntimeException("Schreiben der Konfig in den String schlug fehl", e);
        }
    }

    @Override
    public String toString() {
        return "User: " + userEmail + "\n" +
                "Anzahl Einträge: " + configuration.size() + "\n" +
                configToString();
    }

    public void updateWith(Map<String, String> propertiesUpdate) {
        configuration.putAll(propertiesUpdate);
    }

    public String getEmail() {
        return userEmail;
    }
}
