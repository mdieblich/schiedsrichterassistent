package com.dieblich.handball.schiedsrichterassistent.mail.templates;

import com.dieblich.handball.schiedsrichterassistent.SchiriConfiguration;
import com.dieblich.handball.schiedsrichterassistent.mail.Email;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;

import java.util.List;


public class ConfigConfirmationEmail extends Email {

    public ConfigConfirmationEmail(String botEmailAddress, String schiriEmailAddress, SchiriConfiguration currentConfig, List<String> log) throws JsonProcessingException {
        super(botEmailAddress, schiriEmailAddress,
                "RE: Konfiguration",
                """
                Ich habe deine Konfigurationsänderung erhalten. Die vollständige Konfiguration siehst du unten.
                Solltest du etwas vermissen, so findest du noch weiter unten mein (Fehler-)Protokoll.
                
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
                ---------- FEHLERPROTOKOLL START -----------------------------
                """ + String.join("\n", log) + "\n" + """
                ---------- FEHLERPROTOKOLL ENDE ------------------------------
                """
        );
    }
}
