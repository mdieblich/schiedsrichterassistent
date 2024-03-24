package com.dieblich.handball.schiedsrichterassistent.mail.templates;

import com.dieblich.handball.schiedsrichterassistent.mail.Email;
import com.dieblich.handball.schiedsrichterassistent.mail.UserConfiguration;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;

import static com.dieblich.handball.schiedsrichterassistent.mail.UserConfiguration.SCHIRI_GEOLOCATION;

public class ConfigConfirmationEmail extends Email {

    public static final String SUBJECT = "RE: Konfiguration";
    public ConfigConfirmationEmail(String sender, String receiver, UserConfiguration currentConfig, Session session) throws MessagingException {
        super(session);
        setFrom(sender);
        setTo(receiver);
        setSubject(SUBJECT);
        setContent("""
                Ich habe deine Konfigurationsänderung erhalten. Die vollständige Konfiguration siehst du unten.
                
                Du kannst deine Konfiguration jederzeit ändern, wenn du mir eine Email mit dem Betreff "Konfiguration"
                zuschickst. Inhalt der Email sollten ausschlißelich die Einstellungen sein, die du ändern möchtest.
                """ +
                "Die \""+SCHIRI_GEOLOCATION+"\" wird automatisch aktualisiert, wenn du die Adresse änderst.\n\n"+
                """
                ------------------------------------------------------------
                """ + currentConfig.configToString() + """
                ------------------------------------------------------------
                                
                Viele Grüße,
                der Schiribot
                
                Eine Kreation von Martin Fritz
                """
                );
    }
}
