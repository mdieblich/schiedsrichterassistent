package com.dieblich.handball.schiedsrichterassistent.mail.templates;

import com.dieblich.handball.schiedsrichterassistent.SchiriConfiguration;
import com.dieblich.handball.schiedsrichterassistent.mail.Email;
import com.fasterxml.jackson.core.JsonProcessingException;

public class ConfigConfirmationEmail extends Email {

    public ConfigConfirmationEmail(String botEmailAddress, String schiriEmailAddress, SchiriConfiguration currentConfig) throws JsonProcessingException {
        super(botEmailAddress, schiriEmailAddress,
                "RE: Konfiguration",
                """
                Ich habe deine Konfigurationsänderung erhalten. Die vollständige Konfiguration siehst du unten.
                Solltest du etwas vermissen, so kann es sein, dass Fehler aufgetreten sind. Du erhältst dann eine
                weitere Email mit dem Fehlerprotokoll.
                
                Du kannst deine Konfiguration jederzeit ändern, wenn du mir eine Email mit dem Betreff "Konfiguration"
                zuschickst. Inhalt der Email sollten ausschließlich die Einstellungen sein, die du ändern möchtest.
                """ +
                "Längen- und Breitengrad werden automatisch aktualisiert, wenn du die Adresse änderst.\n\n"+
                """
                ---------- KONFIGURATION START -------------------------------
                """ + currentConfig.toJSON() + """
                ---------- KONFIGURATION ENDE --------------------------------
                
                Viele Grüße,
                der Schiribot
                
                Eine Kreation von Martin Fritz
                """
        );
    }
}
