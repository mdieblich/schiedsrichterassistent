package com.dieblich.handball.schiedsrichterassistent;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

@ToString
@EqualsAndHashCode
@NoArgsConstructor
@SuppressWarnings({"NonAsciiCharacters", "SpellCheckingInspection"})
public class SchiriConfiguration {
    public Benutzerdaten Benutzerdaten = new Benutzerdaten();
    public Spielablauf Spielablauf = new Spielablauf();

    public SchiriConfiguration(String email){
        Benutzerdaten = new Benutzerdaten(email);
    }

    @ToString
    @EqualsAndHashCode
    @NoArgsConstructor
    public static class Benutzerdaten {
        public String Email;
        public String Vorname;
        public String Nachname;
        public String Adresse;
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public Double Längengrad;
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public Double Breitengrad;
        public Benutzerdaten(String email){
            this.Email = email;
        }
    }

    @ToString
    @EqualsAndHashCode
    public static class Spielablauf{
        public Integer UmziehenVorSpiel;
        public Integer PapierKramNachSpiel;
        public Integer UmziehenNachSpiel;
        public TechnischeBesprechung TechnischeBesprechung = new TechnischeBesprechung();

        @ToString
        @EqualsAndHashCode
        public static class TechnischeBesprechung{
            public Integer StandardDauerInMinuten;
            public Map<String, Integer> Abweichungen = new HashMap<>();
        }
    }

    public static SchiriConfiguration NEW_DEFAULT(String email){
        SchiriConfiguration config = new SchiriConfiguration();
        config.Benutzerdaten.Email = email;
        config.Benutzerdaten.Vorname = "Max";
        config.Benutzerdaten.Nachname = "Mustermann";
        config.Benutzerdaten.Adresse = "Musterstr. 17, 54321 Köln";
        config.Spielablauf.UmziehenVorSpiel = 15;
        config.Spielablauf.PapierKramNachSpiel = 15;
        config.Spielablauf.UmziehenNachSpiel = 15;
        config.Spielablauf.TechnischeBesprechung.StandardDauerInMinuten = 30;
        config.Spielablauf.TechnischeBesprechung.Abweichungen.putAll(Map.of(
                // put Map.of inside of HashMap to have it mutable
                "Regionalliga", 45,
                "Oberliga", 45
        ));
        return config;
    }
    public void updateWith(String configUpdate, Function<String, Optional<double[]>> addressToGeoLocation, Consumer<String> log) {
        try {
        SchiriConfiguration newConfig = mapper.readValue(configUpdate, SchiriConfiguration.class);
        if(newConfig.Benutzerdaten != null){
            if(newConfig.Benutzerdaten.Email != null) {Benutzerdaten.Email = newConfig.Benutzerdaten.Email;}
            if(newConfig.Benutzerdaten.Vorname != null) {Benutzerdaten.Vorname = newConfig.Benutzerdaten.Vorname;}
            if(newConfig.Benutzerdaten.Nachname != null) {Benutzerdaten.Nachname = newConfig.Benutzerdaten.Nachname;}

            if(newConfig.Benutzerdaten.Adresse != null) {
                if(newConfig.Benutzerdaten.Längengrad != null && newConfig.Benutzerdaten.Breitengrad != null){
                    // all 3 have been set
                    Benutzerdaten.Adresse = newConfig.Benutzerdaten.Adresse;
                    Benutzerdaten.Längengrad = newConfig.Benutzerdaten.Längengrad;
                    Benutzerdaten.Breitengrad = newConfig.Benutzerdaten.Breitengrad;
                } else {
                    // only address was set, let's search the geolocation
                    Optional<double[]> optionalGeoLocation = addressToGeoLocation.apply(newConfig.Benutzerdaten.Adresse);
                    if(optionalGeoLocation.isPresent()){
                        Benutzerdaten.Adresse = newConfig.Benutzerdaten.Adresse;
                        Benutzerdaten.Längengrad = optionalGeoLocation.get()[0];
                        Benutzerdaten.Breitengrad = optionalGeoLocation.get()[1];
                    }else{
                        log.accept("Für die Adresse \""+newConfig.Benutzerdaten.Adresse+"\" konnten Längen- und Breitengrad nicht bestimmt werden. Sie wird daher nicht übernomen.");
                        log.accept("FALLS DAS PROBLEM WIEDERHOLT AUFTRITT SO KANNST DU FOLGENDES TUN:");
                        log.accept("1. Bestimme mithilfe eines Kartendienstes (z.B. https://www.gpskoordinaten.de/) deinen Längen- und Breitengrad.");
                        log.accept("2. Setze in der Konfiguration im Abschnitt \"Benutzerdaten\" die Adresse UND Werte für \"Löngengrad\"");
                        log.accept("   und \"Breitengrad\". Beachte bitte, dass du min. 4-Nachkommastellen verwendest.");
                        log.accept("   Verwendet wird das Koordinatensystem WGS 84");
                    }
                }
            }
        }
        if(newConfig.Spielablauf != null){
            if(newConfig.Spielablauf.UmziehenVorSpiel != null) {Spielablauf.UmziehenVorSpiel = newConfig.Spielablauf.UmziehenVorSpiel;}
            if(newConfig.Spielablauf.PapierKramNachSpiel != null) {Spielablauf.PapierKramNachSpiel = newConfig.Spielablauf.PapierKramNachSpiel;}
            if(newConfig.Spielablauf.UmziehenNachSpiel != null) {Spielablauf.UmziehenNachSpiel = newConfig.Spielablauf.UmziehenNachSpiel;}
            if(newConfig.Spielablauf.TechnischeBesprechung != null) {
                if(newConfig.Spielablauf.TechnischeBesprechung.StandardDauerInMinuten != null) {
                    Spielablauf.TechnischeBesprechung.StandardDauerInMinuten = newConfig.Spielablauf.TechnischeBesprechung.StandardDauerInMinuten;
                }
                if(newConfig.Spielablauf.TechnischeBesprechung.Abweichungen != null){
                    Spielablauf.TechnischeBesprechung.Abweichungen.putAll(newConfig.Spielablauf.TechnischeBesprechung.Abweichungen);
                }
            }
        }

        } catch (JsonProcessingException e) {
            log.accept("Fehler beim Lesen der Konfiguration: " + e.getMessage());
        }
    }

    /// JSON serialization and deserialization
    private final static ObjectMapper mapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public String toJSON() throws JsonProcessingException {
        return mapper.writeValueAsString(this);
    }

    public static SchiriConfiguration fromJSON(String json) throws JsonProcessingException {
        return mapper.readValue(json, SchiriConfiguration.class);
    }

}
