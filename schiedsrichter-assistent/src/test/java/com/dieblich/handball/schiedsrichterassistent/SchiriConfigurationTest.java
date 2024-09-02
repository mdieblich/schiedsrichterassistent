package com.dieblich.handball.schiedsrichterassistent;

import com.dieblich.handball.schiedsrichterassistent.config.ConfigException;
import com.dieblich.handball.schiedsrichterassistent.config.SchiriConfiguration;
import com.dieblich.handball.schiedsrichterassistent.geo.Koordinaten;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("NonAsciiCharacters")
class SchiriConfigurationTest {

    @Test
    public void hasNiceJSON() throws JsonProcessingException {
        SchiriConfiguration config = SchiriConfiguration.NEW_DEFAULT("muster@max.de");
        config.Benutzerdaten.Vorname = "Max";
        config.Benutzerdaten.Nachname = "Mustermann";
        config.Benutzerdaten.Adresse = "Musterstr. 17, 54321 Köln";
        config.Benutzerdaten.Längengrad  = 1.23456;
        config.Benutzerdaten.Breitengrad = 5.67891;
        config.Gespannpartner.add("mike.blind@loser.com");

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
            "EffektiveSpielDauer" : 90,
            "UmziehenVorSpiel" : 15,
            "PapierKramNachSpiel" : 15,
            "UmziehenNachSpiel" : 15,
            "TechnischeBesprechung" : {
              "StandardDauerInMinuten" : 30,
              "Abweichungen" : {
                "Oberliga" : 45,
                "Regionalliga" : 45
              }
            }
          },
          "Kosten" : {
            "TeilnahmeEntschädigung" : {
              "Standard" : 22.5,
              "Abweichungen": {
                "Oberliga Männer" : 50.0,
                "Oberliga Frauen" : 40.0,
                "Verbandsliga Männer" : 40.0,
                "Verbandsliga Frauen" : 30.0,
                "Landesliga Männer" : 30.0,
                "Landesliga Frauen" : 30.0
              }
            },
            "Fahrer" : {
              "Standard" : 0.35,
              "Abweichungen" : {
                "Oberliga" : 0.3,
                "Verbandsliga" : 0.3,
                "Landesliga" : 0.3
              }
            },
            "Beifahrer" : {
              "Standard" : 0.05,
              "Abweichungen" : {
                "Oberliga" : 0.0,
                "Verbandsliga" : 0.0,
                "Landesliga" : 0.0
              }
            }
          },
          "Gespannpartner": [
            "mike.blind@loser.com"
          ]
        }
        """);

        JsonNode actualTree = mapper.readTree(json);
        assertEquals(expectedTree, actualTree);
    }
    @Test
    public void jsonLeavesOutLongitudeAndLatitude() throws JsonProcessingException {
        SchiriConfiguration config = SchiriConfiguration.NEW_DEFAULT("muster@max.de");
        config.Benutzerdaten.Vorname = "Max";
        config.Benutzerdaten.Nachname = "Mustermann";
        config.Benutzerdaten.Adresse = "Musterstr. 17, 54321 Köln";
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
          },
          "Kosten" : {
            "TeilnahmeEntschädigung" : {
              "Standard" : 22.50,
              "Abweichungen": {
                "Oberliga Männer" : 50.0,
                "Oberliga Frauen" : 40.0,
                "Verbandsliga Männer" : 40.0,
                "Verbandsliga Frauen" : 30.0,
                "Landesliga Männer" : 30.0,
                "Landesliga Frauen" : 30.0
              }
            },
            "Fahrer" : {
              "Standard" : 0.35,
              "Abweichungen" : {
                "Oberliga" : 0.3,
                "Verbandsliga" : 0.3,
                "Landesliga" : 0.3
              }
            },
            "Beifahrer" : {
              "Standard" : 0.05,
              "Abweichungen" : {
                "Oberliga" : 0.0,
                "Verbandsliga" : 0.0,
                "Landesliga" : 0.0
              }
            }
          },
          "Gespannpartner": []
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
          },
          "Kosten" : {
            "TeilnahmeEntschädigung" : {
              "Standard" : 22.50,
              "Abweichungen": {
                "Oberliga Männer" : 50.0,
                "Oberliga Frauen" : 40.0,
                "Verbandsliga Männer" : 40.0,
                "Verbandsliga Frauen" : 30.0,
                "Landesliga Männer" : 30.0,
                "Landesliga Frauen" : 30.0
              }
            },
            "Fahrer" : {
              "Standard" : 0.35,
              "Abweichungen" : {
                "Oberliga" : 0.3,
                "Verbandsliga" : 0.3,
                "Landesliga" : 0.3
              }
            },
            "Beifahrer" : {
              "Standard" : 0.05,
              "Abweichungen" : {
                "Oberliga" : 0.0,
                "Verbandsliga" : 0.0,
                "Landesliga" : 0.0
              }
            }
          }
        }
        """;

        SchiriConfiguration actual = SchiriConfiguration.fromJSON(json);

        SchiriConfiguration expected = SchiriConfiguration.NEW_DEFAULT("muster@max.de");
        expected.Benutzerdaten.Vorname = "Max";
        expected.Benutzerdaten.Nachname = "Mustermann";
        expected.Benutzerdaten.Adresse = "Musterstr. 17, 54321 Köln";
        expected.Benutzerdaten.Längengrad  = 1.23456;
        expected.Benutzerdaten.Breitengrad = 5.67891;

        assertEquals(expected, actual);
    }

    @Test
    public void updateWithNothingChangesNothing() throws ConfigException {
        SchiriConfiguration config = new SchiriConfiguration("muster@moritz.de");
        config.Benutzerdaten.Vorname = "Moritz";
        config.Benutzerdaten.Nachname = "Schulze";
        config.Benutzerdaten.Adresse = "Irgendwo 12, 12345 Berlin";
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

        config.updateWith("{}", fakeAddressToGeoLocation);

        assertEquals("muster@moritz.de", config.Benutzerdaten.Email);
        assertEquals("Moritz", config.Benutzerdaten.Vorname);
        assertEquals("Schulze", config.Benutzerdaten.Nachname);
        assertEquals("Irgendwo 12, 12345 Berlin", config.Benutzerdaten.Adresse);
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
    public void updateWithSingleEntry() throws ConfigException {
        SchiriConfiguration config = new SchiriConfiguration("");
        config.updateWith("""
                {
                	"Spielablauf": {
                		"TechnischeBesprechung": {
                			"StandardDauerInMinuten": 90
                		}
                	}
                }
                """, fakeAddressToGeoLocation);

        assertEquals(90, config.Spielablauf.TechnischeBesprechung.StandardDauerInMinuten);
    }
    @Test
    public void updateWithMultipleEntries() throws ConfigException {
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
                """, fakeAddressToGeoLocation);

        assertEquals("Moritz", config.Benutzerdaten.Vorname);
        assertEquals(90, config.Spielablauf.TechnischeBesprechung.StandardDauerInMinuten);
    }

    @Test
    public void updateWithTechnischeBesprechungLiga() throws ConfigException {
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
                """, fakeAddressToGeoLocation);

        assertEquals(50, config.Spielablauf.TechnischeBesprechung.Abweichungen.get("Regionalliga"));
    }
    @Test
    public void updateWithCreatesLiga() throws ConfigException {
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
                """, fakeAddressToGeoLocation);

        assertEquals(20, config.Spielablauf.TechnischeBesprechung.Abweichungen.get("Kreisklasse"));
    }

    @Test
    public void updateWithAdresseCreatesGeoLocation() throws ConfigException {
        SchiriConfiguration config = new SchiriConfiguration("");
        config.updateWith("""
                {
                	"Benutzerdaten": {
                		"Adresse": "Dudelstr. 15, 12345 Dudelstadt"
                	}
                }
                """, String -> Optional.of(new Koordinaten(5,3)));

        assertEquals("Dudelstr. 15, 12345 Dudelstadt", config.Benutzerdaten.Adresse);
        assertEquals(3, config.Benutzerdaten.Längengrad);
        assertEquals(5, config.Benutzerdaten.Breitengrad);
    }
    @Test
    public void updateWithUnknownAdresseChangesNothing(){
        SchiriConfiguration config = new SchiriConfiguration("");
        String adresseBefore = config.Benutzerdaten.Adresse;
        try {
            config.updateWith("""
                    {
                    	"Benutzerdaten": {
                    		"Adresse": "Dudelstr. 15, 12345 Dudelstadt"
                    	}
                    }
                    """, String -> Optional.empty());
            fail("Exception was expected to happen");
        } catch (ConfigException e){
            // so far, so good
        }

        assertEquals(adresseBefore, config.Benutzerdaten.Adresse);
        assertNull(config.Benutzerdaten.Längengrad);
        assertNull(config.Benutzerdaten.Breitengrad);
    }
    @Test
    public void updateWithUnknownAdresseCreatesLogEntry() {
        SchiriConfiguration config = new SchiriConfiguration("");
        try {
            config.updateWith("""
                    {
                    	"Benutzerdaten": {
                    		"Adresse": "Dudelstr. 15, 12345 Dudelstadt"
                    	}
                    }
                    """, String -> Optional.empty());
            fail("Exception was not thrown");
        } catch (ConfigException e){
            String expectedMessage =
                    """
                            Für die Adresse "Dudelstr. 15, 12345 Dudelstadt" konnten Längen- und Breitengrad nicht bestimmt werden. Sie wird daher nicht übernomen.
                            FALLS DAS PROBLEM WIEDERHOLT AUFTRITT SO KANNST DU FOLGENDES TUN:
                            1. Bestimme mithilfe eines Kartendienstes (z.B. https://www.gpskoordinaten.de/) deinen Längen- und Breitengrad.
                            2. Setze in der Konfiguration im Abschnitt "Benutzerdaten" die Adresse UND Werte für "Längengrad" \
                            und "Breitengrad". Beachte bitte, dass du min. 4-Nachkommastellen verwendest. \
                            Verwendet wird das Koordinatensystem WGS 84""";
            assertEquals(expectedMessage, e.getCause().getMessage());
        }
    }

    @Test
    public void updateWithUnknownAdresseButGeoLocationWorks() throws ConfigException {
        SchiriConfiguration config = new SchiriConfiguration("");
        config.updateWith("""
                {
                	"Benutzerdaten": {
                		"Adresse": "Dudelstr. 15, 12345 Dudelstadt",
                		"Längengrad": 3,
                		"Breitengrad": 5
                	}
                }
                """, fakeAddressToGeoLocation);

        assertEquals("Dudelstr. 15, 12345 Dudelstadt", config.Benutzerdaten.Adresse);
        assertEquals(3, config.Benutzerdaten.Längengrad);
        assertEquals(5, config.Benutzerdaten.Breitengrad);
    }

    @Test
    public void updateWithUnknownEntryDoesNothing() throws ConfigException {
        SchiriConfiguration config = new SchiriConfiguration("");
        config.updateWith("""
                {
                	"Spielablauf": {
                		"Grillen": "gerne"
                	}
                }
                """, fakeAddressToGeoLocation);
        // Nothing happens
    }

    @Test
    public void updateWithWrongTypeDoesNothing(){
        SchiriConfiguration config = SchiriConfiguration.NEW_DEFAULT("");
        int umziehenVorher = config.Spielablauf.UmziehenVorSpiel;
        try {
            config.updateWith("""
                    {
                        "Spielablauf": {
                            "UmziehenVorSpiel": "Ein String!"
                        }
                    }
                    """, fakeAddressToGeoLocation);
            fail("Exception was excpected");
        } catch (ConfigException e) {
            // so far, so good
        }

        assertEquals(umziehenVorher, config.Spielablauf.UmziehenVorSpiel);
    }
    @Test
    public void updateWithInvalidJSONCreatesException() {
        SchiriConfiguration config = SchiriConfiguration.NEW_DEFAULT("");
        try {
            config.updateWith("""
                    {
                        "Spielablauf": {
                            "UmziehenVorSpiel": 25,
                            "UmziehenNachSpiel
                    
                    }
                    """, fakeAddressToGeoLocation);
            fail("Exception not thrown");
        } catch (ConfigException e) {
            // everything is fine
        }
    }

    @Test
    public void updateWithCreatesTeilnahmeEntschädigung() throws ConfigException {
        SchiriConfiguration config = new SchiriConfiguration("");
        config.updateWith("""
                {
                	"Kosten" : {
                        "TeilnahmeEntschädigung" : {
                            "Abweichungen": {
                                "Regionalliga Männer" : 100.0
                            }
                        }
                      }
                }
                """, fakeAddressToGeoLocation);

        assertEquals(100d, config.Kosten.TeilnahmeEntschädigung.get("Regionalliga Männer"));
    }

    @Test
    public void updateWithCreatesFahrtkosten() throws ConfigException {
        SchiriConfiguration config = new SchiriConfiguration("");
        config.updateWith("""
                {
                	"Kosten" : {
                        "Fahrer" : {
                          "Abweichungen" : {
                            "Regionalliga" : 0.9
                          }
                        }
                      }
                }
                """, fakeAddressToGeoLocation);

        assertEquals(0.9, config.Kosten.Fahrer.get("Regionalliga"));
    }

    @Test
    public void updateWithCreatesBeifahrerKosten() throws ConfigException {
        SchiriConfiguration config = new SchiriConfiguration("");
        config.updateWith("""
                {
                	"Kosten" : {
                        "Beifahrer" : {
                          "Abweichungen" : {
                            "Regionalliga" : 0.30
                          }
                        }
                      }
                }
                """, fakeAddressToGeoLocation);

        assertEquals(0.3, config.Kosten.Beifahrer.get("Regionalliga"));
    }

    @Test
    public void getFahrtkostenForSchortLigaName(){
        SchiriConfiguration config = SchiriConfiguration.NEW_DEFAULT("");
        config.Kosten.Fahrer.Abweichungen.put("Verbandsliga", 0.9d);
        assertEquals(0.9d, config.Kosten.Fahrer.get("Mittelrhein Verbandsliga Frauen"));
    }
    @Test
    public void getBeifahrerkostenForSchortLigaName(){
        SchiriConfiguration config = SchiriConfiguration.NEW_DEFAULT("");
        config.Kosten.Beifahrer.Abweichungen.put("Verbandsliga", 0.9d);
        assertEquals(0.9d, config.Kosten.Beifahrer.get("Mittelrhein Verbandsliga Frauen"));
    }

    @Test
    public void setEmailWillBeLowerCase() throws ConfigException {
        SchiriConfiguration config = SchiriConfiguration.NEW_DEFAULT("");
        config.updateWith("""
                {
                 	"Benutzerdaten": {
                        "Email": "Martin@nirgendwo.de"
                 	}
                }
                """, fakeAddressToGeoLocation);
        assertEquals("martin@nirgendwo.de", config.Benutzerdaten.Email);
    }
    @Test
    public void setWhitelistWillBeLowerCase() throws ConfigException {
        SchiriConfiguration config = SchiriConfiguration.NEW_DEFAULT("");
        config.updateWith("""
                {
                    "Gespannpartner": [
                        "Martin@nirgendwo.de"
                    ]
                }
                """, fakeAddressToGeoLocation);
        assertEquals(List.of("martin@nirgendwo.de"), config.Gespannpartner);
    }

    @Test
    public void hasGespannpartnerFindsNothing(){
        SchiriConfiguration configA = SchiriConfiguration.NEW_DEFAULT("");
        configA.Gespannpartner.add("Martin@nirgendwo.de");
        SchiriConfiguration configB = SchiriConfiguration.NEW_DEFAULT("thomas@nirgendwo.de");
        assertFalse(configA.hasGespannpartner(configB));
    }
    @Test
    public void hasGespannpartnerIgnoresCase(){
        SchiriConfiguration configA = SchiriConfiguration.NEW_DEFAULT("");
        configA.Gespannpartner.add("Martin@nirgendwo.de");
        SchiriConfiguration configB = SchiriConfiguration.NEW_DEFAULT("marTIN@nirgendwo.DE");
        assertTrue(configA.hasGespannpartner(configB));
    }

    private final Function<String, Optional<Koordinaten>> fakeAddressToGeoLocation = (String) -> Optional.empty();
}