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

        public void updateWith(Benutzerdaten other, Function<String, Optional<double[]>> addressToGeoLocation, Consumer<String> log) {
            if(other.Email != null) {Email = other.Email;}
            if(other.Vorname != null) {Vorname = other.Vorname;}
            if(other.Nachname != null) {Nachname = other.Nachname;}

            if(other.Adresse != null) {
                if(other.Längengrad != null && other.Breitengrad != null){
                    // all 3 have been set
                    Adresse = other.Adresse;
                    Längengrad = other.Längengrad;
                    Breitengrad = other.Breitengrad;
                } else {
                    // only address was set, let's search the geolocation
                    Optional<double[]> optionalGeoLocation = addressToGeoLocation.apply(other.Adresse);
                    if(optionalGeoLocation.isPresent()){
                        Adresse = other.Adresse;
                        Längengrad = optionalGeoLocation.get()[0];
                        Breitengrad = optionalGeoLocation.get()[1];
                    }else{
                        log.accept("Für die Adresse \""+other.Adresse+"\" konnten Längen- und Breitengrad nicht bestimmt werden. Sie wird daher nicht übernomen.");
                        log.accept("FALLS DAS PROBLEM WIEDERHOLT AUFTRITT SO KANNST DU FOLGENDES TUN:");
                        log.accept("1. Bestimme mithilfe eines Kartendienstes (z.B. https://www.gpskoordinaten.de/) deinen Längen- und Breitengrad.");
                        log.accept("2. Setze in der Konfiguration im Abschnitt \"Benutzerdaten\" die Adresse UND Werte für \"Löngengrad\"");
                        log.accept("   und \"Breitengrad\". Beachte bitte, dass du min. 4-Nachkommastellen verwendest.");
                        log.accept("   Verwendet wird das Koordinatensystem WGS 84");
                    }
                }
            }
        }
    }

    @ToString
    @EqualsAndHashCode
    public static class Spielablauf{
        public Integer UmziehenVorSpiel;
        public Integer PapierKramNachSpiel;
        public Integer UmziehenNachSpiel;
        public TechnischeBesprechung TechnischeBesprechung = new TechnischeBesprechung();

        public void updateWith(Spielablauf other) {
            if(other.UmziehenVorSpiel != null) {UmziehenVorSpiel = other.UmziehenVorSpiel;}
            if(other.PapierKramNachSpiel != null) {PapierKramNachSpiel = other.PapierKramNachSpiel;}
            if(other.UmziehenNachSpiel != null) {UmziehenNachSpiel = other.UmziehenNachSpiel;}
            if(other.TechnischeBesprechung != null) {
                TechnischeBesprechung.updateWith(other.TechnischeBesprechung);
            }
        }

        @ToString
        @EqualsAndHashCode
        public static class TechnischeBesprechung{
            public Integer StandardDauerInMinuten;
            public Map<String, Integer> Abweichungen = new HashMap<>();

            public void updateWith(TechnischeBesprechung other) {
                if(other.StandardDauerInMinuten != null) {StandardDauerInMinuten = other.StandardDauerInMinuten;                }
                if(other.Abweichungen != null){Abweichungen.putAll(other.Abweichungen);}
            }
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
                // put Map.of inside HashMap to have it mutable
                "Regionalliga", 45,
                "Oberliga", 45
        ));
        return config;
    }
    public void updateWith(String configUpdate, Function<String, Optional<double[]>> addressToGeoLocation, Consumer<String> log) {
        try {
            SchiriConfiguration newConfig = mapper.readValue(configUpdate, SchiriConfiguration.class);
            updateWith(newConfig, addressToGeoLocation, log);
        } catch (JsonProcessingException e) {
            log.accept("Fehler beim Lesen der Konfiguration: " + e.getMessage());
        }
    }

    public void updateWith(SchiriConfiguration other, Function<String, Optional<double[]>> addressToGeoLocation, Consumer<String> log) {
        if(other.Benutzerdaten != null){
            Benutzerdaten.updateWith(other.Benutzerdaten, addressToGeoLocation, log);
        }
        if(other.Spielablauf != null){
            Spielablauf.updateWith(other.Spielablauf);
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
