package com.dieblich.handball.schiedsrichterassistent.mail;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

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
        config.configuration.put("Umziehen.DauerInMinuten", "15");
        config.configuration.put("TechnischeBesprechung.Oberliga.DauerInMinuten", "45");
        config.configuration.put("TechnischeBesprechung.UnterOberliga.DauerInMinuten", "30");
        return config;
    }

    private UserConfiguration(String userEmail){
        this.userEmail = userEmail;
    }

    public void set(String key, String value) {
        configuration.put(key, value);
    }

    public Message toMessage(Session session) throws MessagingException {
        MimeMessage message = new MimeMessage(session);
        message.setSubject("Konfiguration vom " + now());
        message.setFrom(new InternetAddress(userEmail));
        message.setText(configToString());
        return message;
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
