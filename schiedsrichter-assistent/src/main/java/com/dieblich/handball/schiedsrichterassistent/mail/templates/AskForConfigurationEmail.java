package com.dieblich.handball.schiedsrichterassistent.mail.templates;

import com.dieblich.handball.schiedsrichterassistent.mail.Email;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;

public class AskForConfigurationEmail extends Email {

    public AskForConfigurationEmail(String botEmailAddress, String schiriEmailAddress) {
        super(botEmailAddress, schiriEmailAddress,"Konfiguration nicht vollständig","""
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
            
            Deine Email an mich wurde gelöscht.
            
            Viele Grüße,
            der Schiribot
            
            Eine Kreation von Martin Fritz
            """
        );
    }
}
