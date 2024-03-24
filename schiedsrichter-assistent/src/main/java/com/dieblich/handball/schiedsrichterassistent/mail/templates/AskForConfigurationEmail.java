package com.dieblich.handball.schiedsrichterassistent.mail.templates;

import com.dieblich.handball.schiedsrichterassistent.mail.Email;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;

import java.util.List;
import java.util.stream.Collectors;

public class AskForConfigurationEmail extends Email {

    public static final String SUBJECT = "Konfiguration nicht vollständig";
    public AskForConfigurationEmail(String botEmailAddress, String schiriEmailAddress, List<String> missingConfigKeys, Session session) throws MessagingException {
        super(session);
        setFrom(botEmailAddress);
        setTo(schiriEmailAddress);
        setSubject(SUBJECT);
        setContent("""
            Hallo!
            
            Ich habe eine Email von dir erhalten (vermutlich mit einer Ansetzung). Deine Konfiguration ist aber noch
            nicht vollständig. Bitte schick mir daher zuerst eine Email mit dem Betreff "Konfiguration", wo in den 
            ersten Zeilen folgendes steht:
            
            """+
            joinConfigKeys(missingConfigKeys)+"\n"+
            """
            
            Besten Dank. Hier nochmal zusammengefasst, wofür die Daten benötigt werden:
            - Vor- & Nachname um dich in den Ansetzungsemails zu identifieren
            - Adresse um die Route zur Halle zu berechnen
            
            Viele Grüße,
            der Schiribot
            
            Eine Kreation von Martin Fritz
            """
        );
    }

    private static String joinConfigKeys(List<String> missingConfigKeys){
        return missingConfigKeys.stream()
                .map(s -> s+"=")
                .collect(Collectors.joining("\n"));
    }
}
