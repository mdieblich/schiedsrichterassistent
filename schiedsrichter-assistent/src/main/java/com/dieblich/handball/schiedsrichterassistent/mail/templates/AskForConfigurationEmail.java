package com.dieblich.handball.schiedsrichterassistent.mail.templates;

import com.dieblich.handball.schiedsrichterassistent.mail.Email;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;

import java.util.List;
import java.util.stream.Collectors;

public class AskForConfigurationEmail extends Email {

    public static final String SUBJECT = "Konfiguration nicht vollständig";
    public AskForConfigurationEmail(String botEmailAddress, String schiriEmailAddress, Session session) throws MessagingException {
        super(botEmailAddress, schiriEmailAddress, session);
        setSubject(SUBJECT);
        setContent("""
            Hallo!
            
            Ich habe eine Email von dir erhalten (vermutlich mit einer Ansetzung). Deine Konfiguration ist aber noch
            nicht vollständig. Bitte schick mir daher zuerst eine Email mit dem Betreff "Konfiguration", in der
            folgendes steht:
            
            {
                "Benutzerdaten": {
                    "Vorname": "Max",
                    "Nachname": "Mustermann",
                    "Adresse": "Musterstr. 17, 54321 Köln"
                }
            }
            
            Besten Dank. Hier nochmal zusammengefasst, wofür die Daten benötigt werden:
            - Vor- & Nachname um dich in den Ansetzungsemails zu identifieren
            - Adresse um die Route zur Halle zu berechnen
            
            Viele Grüße,
            der Schiribot
            
            Eine Kreation von Martin Fritz
            """
        );
    }
}
