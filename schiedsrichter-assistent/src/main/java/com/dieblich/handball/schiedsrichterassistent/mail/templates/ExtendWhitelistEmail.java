package com.dieblich.handball.schiedsrichterassistent.mail.templates;

import com.dieblich.handball.schiedsrichterassistent.SchiriConfiguration;
import com.dieblich.handball.schiedsrichterassistent.mail.Email;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ExtendWhitelistEmail extends Email {
    public ExtendWhitelistEmail(String botEmailAddress, SchiriConfiguration schiriConfig, SchiriConfiguration.Benutzerdaten otherSchiri){
        super(botEmailAddress, schiriConfig.Benutzerdaten.Email,
        "Du musst deinen Partner autorisieren","""
                Hallo!
                
                Ich habe eine Ansetzungsemail von \"""" + otherSchiri.Vorname + " " + otherSchiri.Nachname + "\" " + """
                bekommen.
                Du wirst dort als Gespannpartner erwähnt, aber du hast sie/ihn nicht in deiner Liste der Gespannpartner.
                
                Wenn du möchtest, dass ich für euch Ansetzungsemails bearbeite, so musst du sie/ihn dort ergänzen. Dies
                geht, in dem du mir eine Email mit dem Betreff "Konfiguration" zusendest, wo die Gespannpartnerliste wie
                folgt erweitert ist:
                
                """+createUpdatedGespannpartnerListe(schiriConfig.Gespannpartner, otherSchiri.Email)+
                """
                
                Bitte beachte, dass du mir immer die vollständige Liste deiner Gespannpartner zuschickst, da ich deine
                Liste immer überschreibe.
                
                Viele Grüße,
                der Schiribot
                """);
    }

    private static String createUpdatedGespannpartnerListe(List<String> gespannpartner, String otherSchiriEmail){
        List<String> extendedList = new ArrayList<>(gespannpartner);
        extendedList.add(otherSchiriEmail);
        return "{\n" +
                "  \"Gespannpartner\": [\n" +
                extendedList.stream()
                        .map(s -> "    \"" +s + "\"")
                        .collect(Collectors.joining(",\n"))+ "\n" +
                "  ]\n" +
                "}";
    }
}
