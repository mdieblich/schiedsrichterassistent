package com.dieblich.handball.schiedsrichterassistent.mail.templates;

import com.dieblich.handball.schiedsrichterassistent.mail.Email;
import com.dieblich.handball.schiedsrichterassistent.mail.UserConfiguration;
import com.dieblich.handball.schiedsrichterassistent.mail.UserLog;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;

import static com.dieblich.handball.schiedsrichterassistent.mail.UserConfiguration.SCHIRI_GEOLOCATION;

public class ConfigConfirmationEmail extends Email {

    public static final String SUBJECT = "RE: Konfiguration";
    public ConfigConfirmationEmail(String sender, String receiver, UserConfiguration currentConfig, UserLog log, Session session) throws MessagingException {
        super(session);
        setFrom(sender);
        setTo(receiver);
        setSubject(SUBJECT);
        setContent("""
                Ich habe deine Konfigurationsänderung erhalten. Die vollständige Konfiguration siehst du unten.
                Solltest du etwas vermissen, so findest du noch weiter unten mein (Fehler-)Protokoll. 
                
                Du kannst deine Konfiguration jederzeit ändern, wenn du mir eine Email mit dem Betreff "Konfiguration"
                zuschickst. Inhalt der Email sollten ausschließlich die Einstellungen sein, die du ändern möchtest.
                """ +
                "Die \""+SCHIRI_GEOLOCATION+"\" wird automatisch aktualisiert, wenn du die Adresse änderst.\n\n"+
                """
                ---------- KONFIGURATION START -------------------------------
                """ + currentConfig.configToString() + """
                ---------- KONFIGURATION ENDE --------------------------------
                                
                Viele Grüße,
                der Schiribot
                
                Eine Kreation von Martin Fritz
                ---------- FEHLERPROTOKOLL START -----------------------------
                """ + log.createOutput() + """
                ---------- FEHLERPROTOKOLL ENDE ------------------------------
                """
                );
    }
}
