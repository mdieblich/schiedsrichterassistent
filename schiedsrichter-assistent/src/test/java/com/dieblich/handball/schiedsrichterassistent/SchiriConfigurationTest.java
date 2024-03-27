package com.dieblich.handball.schiedsrichterassistent;

import com.dieblich.handball.schiedsrichterassistent.geo.Koordinaten;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("NonAsciiCharacters")
class SchiriConfigurationTest {

    @Test
    public void hasNiceJSON() throws JsonProcessingException {
        SchiriConfiguration config = SchiriConfiguration.NEW_DEFAULT("muster@max.de");
        config.Benutzerdaten.Längengrad  = 1.23456;
        config.Benutzerdaten.Breitengrad = 5.67891;

        String json = config.toJSON();

        ObjectMapper mapper = new ObjectMapper();
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
            "EffektiveSpielDauer" : 90,
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
        SchiriConfiguration config = SchiriConfiguration.NEW_DEFAULT("muster@max.de");
        config.Benutzerdaten.Längengrad  = null;
        config.Benutzerdaten.Breitengrad = null;

        String json = config.toJSON();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode expectedTree = mapper.readTree("""
        {
          "Benutzerdaten" : {
            "Email" : "muster@max.de",
            "Vorname" : "Max",
            "Nachname" : "Mustermann",
            "Adresse" : "Musterstr. 17, 54321 Köln"
          },
          "Spielablauf" : {
            "UmziehenVorSpiel" : 15,
            "EffektiveSpielDauer" : 90,
            "PapierKramNachSpiel" : 15,
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
    public void canReadFromJSON() throws JsonProcessingException {
        String json = """
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
            "EffektiveSpielDauer" : 90,
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
        """;

        SchiriConfiguration actual = SchiriConfiguration.fromJSON(json);

        SchiriConfiguration expected = SchiriConfiguration.NEW_DEFAULT("muster@max.de");
        expected.Benutzerdaten.Längengrad  = 1.23456;
        expected.Benutzerdaten.Breitengrad = 5.67891;

        assertEquals(expected, actual);
    }

    @Test
    public void updateWithNothingChangesNothing() {
        SchiriConfiguration config = new SchiriConfiguration("muster@moritz.de");
        config.Benutzerdaten.Vorname = "Moritz";
        config.Benutzerdaten.Nachname = "Schulze";
        config.Benutzerdaten.Adresse = "Irgendo 12, 12345 Berlin";
        config.Benutzerdaten.Längengrad = 5.0;
        config.Benutzerdaten.Breitengrad = 6.0;
        config.Spielablauf.UmziehenVorSpiel = 20;
        config.Spielablauf.EffektiveSpielDauer = 99;
        config.Spielablauf.PapierKramNachSpiel = 5;
        config.Spielablauf.UmziehenNachSpiel = 25;
        config.Spielablauf.TechnischeBesprechung.StandardDauerInMinuten = 90;
        config.Spielablauf.TechnischeBesprechung.Abweichungen.putAll(Map.of(
                "Regionalliga", 100,
                "Oberliga", 60,
                "Kreisklasse", 4
        ));

        config.updateWith("{}", fakeAddressToGeoLocation, fakeLog);

        assertEquals("muster@moritz.de", config.Benutzerdaten.Email);
        assertEquals("Moritz", config.Benutzerdaten.Vorname);
        assertEquals("Schulze", config.Benutzerdaten.Nachname);
        assertEquals("Irgendo 12, 12345 Berlin", config.Benutzerdaten.Adresse);
        assertEquals(5.0, config.Benutzerdaten.Längengrad);
        assertEquals(6.0, config.Benutzerdaten.Breitengrad);
        assertEquals(20, config.Spielablauf.UmziehenVorSpiel);
        assertEquals(99, config.Spielablauf.EffektiveSpielDauer);
        assertEquals(5, config.Spielablauf.PapierKramNachSpiel );
        assertEquals(25, config.Spielablauf.UmziehenNachSpiel );
        assertEquals(90, config.Spielablauf.TechnischeBesprechung.StandardDauerInMinuten);
        assertEquals(Map.of(
        "Regionalliga", 100,
        "Oberliga", 60,
        "Kreisklasse", 4
        ), config.Spielablauf.TechnischeBesprechung.Abweichungen);
    }
    @Test
    public void updateWithSingleEntry() {
        SchiriConfiguration config = new SchiriConfiguration("");
        config.updateWith("""
                {
                	"Spielablauf": {
                		"TechnischeBesprechung": {
                			"StandardDauerInMinuten": 90
                		}
                	}
                }
                """, fakeAddressToGeoLocation, fakeLog);

        assertEquals(90, config.Spielablauf.TechnischeBesprechung.StandardDauerInMinuten);
    }
    @Test
    public void updateWithMultipleEntries() {
        SchiriConfiguration config = new SchiriConfiguration("");
        config.updateWith("""
                {
                 	"Benutzerdaten": {
                        "Vorname": "Moritz"
                 	},
                	"Spielablauf": {
                		"TechnischeBesprechung": {
                			"StandardDauerInMinuten": 90
                		}
                	}
                }
                """, fakeAddressToGeoLocation, fakeLog);

        assertEquals("Moritz", config.Benutzerdaten.Vorname);
        assertEquals(90, config.Spielablauf.TechnischeBesprechung.StandardDauerInMinuten);
    }

    @Test
    public void updateWithTechnischeBesprechungLiga() {
        SchiriConfiguration config = new SchiriConfiguration("");
        config.updateWith("""
                {
                	"Spielablauf": {
                		"TechnischeBesprechung": {
                            "Abweichungen": {
                                "Regionalliga": 50
                            }
                		}
                	}
                }
                """, fakeAddressToGeoLocation, fakeLog);

        assertEquals(50, config.Spielablauf.TechnischeBesprechung.Abweichungen.get("Regionalliga"));
    }
    @Test
    public void updateWithCreatesLiga() {
        SchiriConfiguration config = new SchiriConfiguration("");
        config.updateWith("""
                {
                	"Spielablauf": {
                		"TechnischeBesprechung": {
                            "Abweichungen": {
                                "Kreisklasse": 20
                            }
                		}
                	}
                }
                """, fakeAddressToGeoLocation, fakeLog);

        assertEquals(20, config.Spielablauf.TechnischeBesprechung.Abweichungen.get("Kreisklasse"));
    }

    @Test
    public void updateWithAdresseCreatesGeoLocation() {
        SchiriConfiguration config = new SchiriConfiguration("");
        config.updateWith("""
                {
                	"Benutzerdaten": {
                		"Adresse": "Dudelstr. 15, 12345 Dudelstadt"
                	}
                }
                """, String -> Optional.of(new Koordinaten(5,3)), fakeLog);

        assertEquals("Dudelstr. 15, 12345 Dudelstadt", config.Benutzerdaten.Adresse);
        assertEquals(3, config.Benutzerdaten.Längengrad);
        assertEquals(5, config.Benutzerdaten.Breitengrad);
    }
    @Test
    public void updateWithUnknwonAdresseChangesNothing() {
        SchiriConfiguration config = new SchiriConfiguration("");
        String adresseBefore = config.Benutzerdaten.Adresse;
        config.updateWith("""
                {
                	"Benutzerdaten": {
                		"Adresse": "Dudelstr. 15, 12345 Dudelstadt"
                	}
                }
                """, String -> Optional.empty(), fakeLog);

        assertEquals(adresseBefore, config.Benutzerdaten.Adresse);
        assertNull(config.Benutzerdaten.Längengrad);
        assertNull(config.Benutzerdaten.Breitengrad);
    }
    @Test
    public void updateWithUnknwonAdresseCreatesLogEntry() {
        SchiriConfiguration config = new SchiriConfiguration("");
        List<String> log = new ArrayList<>();
        config.updateWith("""
                {
                	"Benutzerdaten": {
                		"Adresse": "Dudelstr. 15, 12345 Dudelstadt"
                	}
                }
                """, String -> Optional.empty(), log::add);

        List<String> expectedLog = List.of(
            "Für die Adresse \"Dudelstr. 15, 12345 Dudelstadt\" konnten Längen- und Breitengrad nicht bestimmt werden. Sie wird daher nicht übernomen.",
            "FALLS DAS PROBLEM WIEDERHOLT AUFTRITT SO KANNST DU FOLGENDES TUN:",
            "1. Bestimme mithilfe eines Kartendienstes (z.B. https://www.gpskoordinaten.de/) deinen Längen- und Breitengrad.",
            "2. Setze in der Konfiguration im Abschnitt \"Benutzerdaten\" die Adresse UND Werte für \"Löngengrad\"",
            "   und \"Breitengrad\". Beachte bitte, dass du min. 4-Nachkommastellen verwendest.",
            "   Verwendet wird das Koordinatensystem WGS 84"
        );

        assertEquals(expectedLog, log);
    }

    @Test
    public void updateWithUnknwonAdresseButGeoLocationWorks() {
        SchiriConfiguration config = new SchiriConfiguration("");
        config.updateWith("""
                {
                	"Benutzerdaten": {
                		"Adresse": "Dudelstr. 15, 12345 Dudelstadt",
                		"Längengrad": 3,
                		"Breitengrad": 5
                	}
                }
                """, fakeAddressToGeoLocation, fakeLog);

        assertEquals("Dudelstr. 15, 12345 Dudelstadt", config.Benutzerdaten.Adresse);
        assertEquals(3, config.Benutzerdaten.Längengrad);
        assertEquals(5, config.Benutzerdaten.Breitengrad);
    }

    @Test
    public void updateWithUnknownEntryDoesNothing() {
        SchiriConfiguration config = new SchiriConfiguration("");
        config.updateWith("""
                {
                	"Spielablauf": {
                		"Grillen": "gerne"
                	}
                }
                """, fakeAddressToGeoLocation, fakeLog);
        // Nothing happens
    }

    @Test
    public void updateWithWrongTypeDoesNothing() {
        SchiriConfiguration config = SchiriConfiguration.NEW_DEFAULT("");
        int umziehenVorher = config.Spielablauf.UmziehenVorSpiel;
        config.updateWith("""
                {
                	"Spielablauf": {
                        "UmziehenVorSpiel": "Ein String!"
                	}
                }
                """, fakeAddressToGeoLocation, fakeLog);

        assertEquals(umziehenVorher, config.Spielablauf.UmziehenVorSpiel);
    }
    @Test
    public void updateWithInvalidJSONCreatesLog() {
        SchiriConfiguration config = SchiriConfiguration.NEW_DEFAULT("");
        List<String> log = new ArrayList<>();
        config.updateWith("""
                {
                	"Spielablauf": {
                        "UmziehenVorSpiel": 25,
                        "UmziehenNachSpiel
                	
                }
                """, fakeAddressToGeoLocation, log::add);

        assertEquals(1, log.size());
    }

    private final Function<String, Optional<Koordinaten>> fakeAddressToGeoLocation = (String) -> Optional.empty();
    private final Consumer<String> fakeLog = (String) -> {};


}