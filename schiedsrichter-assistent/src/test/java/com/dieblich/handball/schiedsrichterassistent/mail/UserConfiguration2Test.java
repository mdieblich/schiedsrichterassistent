package com.dieblich.handball.schiedsrichterassistent.mail;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserConfiguration2Test {

    @Test
    public void hasNiceJSON() throws JsonProcessingException {
        UserConfiguration2 config = new UserConfiguration2("muster@max.de");
        config.Benutzerdaten.Längengrad  = 1.23456;
        config.Benutzerdaten.Breitengrad = 5.67891;

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(config);

        JsonNode expectedTree = mapper.readTree("""
        {
          "Benutzerdaten" : {
            "Email" : "muster@max.de",
            "Vorname" : "Max",
            "Nachname" : "Mustermann",
            "Adresse" : "Musterstr. 17, 54321 Köln",
            "Längengrad" : 1.23456,
            "Breitengrad" : 5.67891
          },
          "Spielablauf" : {
            "PapierKramNachSpiel" : 15,
            "UmziehenVorSpiel" : 15,
            "UmziehenNachSpiel" : 15,
            "TechnischeBesprechung" : {
              "StandardDauerInMinuten" : 30,
              "Abweichungen" : {
                "Oberliga" : 45,
                "Regionalliga" : 45
              }
            }
          }
        }
        """);

        JsonNode actualTree = mapper.readTree(json);
        assertEquals(expectedTree, actualTree);
    }
    @Test
    public void jsonLeavesOutLongitudeAndLatitude() throws JsonProcessingException {
        UserConfiguration2 config = new UserConfiguration2("muster@max.de");
        config.Benutzerdaten.Längengrad  = null;
        config.Benutzerdaten.Breitengrad = null;

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(config);

        JsonNode expectedTree = mapper.readTree("""
        {
          "Benutzerdaten" : {
            "Email" : "muster@max.de",
            "Vorname" : "Max",
            "Nachname" : "Mustermann",
            "Adresse" : "Musterstr. 17, 54321 Köln"
          },
          "Spielablauf" : {
            "PapierKramNachSpiel" : 15,
            "UmziehenVorSpiel" : 15,
            "UmziehenNachSpiel" : 15,
            "TechnischeBesprechung" : {
              "StandardDauerInMinuten" : 30,
              "Abweichungen" : {
                "Oberliga" : 45,
                "Regionalliga" : 45
              }
            }
          }
        }
        """);

        JsonNode actualTree = mapper.readTree(json);
        assertEquals(expectedTree, actualTree);
    }
}