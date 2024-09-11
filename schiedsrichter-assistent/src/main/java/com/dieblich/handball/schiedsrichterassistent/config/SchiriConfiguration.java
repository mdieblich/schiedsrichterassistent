package com.dieblich.handball.schiedsrichterassistent.config;

import com.dieblich.handball.schiedsrichterassistent.Schiedsrichter;
import com.dieblich.handball.schiedsrichterassistent.geo.Koordinaten;
import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.*;
import java.util.function.Function;

@ToString
@EqualsAndHashCode
@NoArgsConstructor
@SuppressWarnings({"NonAsciiCharacters", "SpellCheckingInspection"})
public class SchiriConfiguration {
    public Benutzerdaten Benutzerdaten = new Benutzerdaten();
    public Spielablauf Spielablauf = new Spielablauf();
    public List<String> Gespannpartner = new ArrayList<>();

    public SchiriConfiguration(String email){
        Benutzerdaten = new Benutzerdaten(email);
    }

    @JsonIgnore
    public boolean isComplete() {
        if(Benutzerdaten == null || Spielablauf == null){
            return false;
        }
        return Benutzerdaten.isComplete() && Spielablauf.isComplete();
    }

    public boolean hasGespannpartner(SchiriConfiguration configOtherSchiri) {
        for(String email:Gespannpartner){
            if(email.equalsIgnoreCase(configOtherSchiri.Benutzerdaten.Email)){
                return true;
            }
        }
        return false;
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

        public void updateWith(Benutzerdaten other, Function<String, Optional<Koordinaten>> addressToKoordinaten) throws ConfigException {
            if(other.Email != null) {Email = other.Email.toLowerCase();}
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
                    Optional<Koordinaten> optionalKoordinaten = addressToKoordinaten.apply(other.Adresse);
                    if(optionalKoordinaten.isPresent()){
                        Adresse = other.Adresse;
                        Längengrad = optionalKoordinaten.get().längengrad();
                        Breitengrad = optionalKoordinaten.get().breitengrad();
                    }else{
                        throw new ConfigException(
                                "Für die Adresse \""+other.Adresse+"\" konnten Längen- und Breitengrad nicht bestimmt werden. Sie wird daher nicht übernomen.\n" +
                                "FALLS DAS PROBLEM WIEDERHOLT AUFTRITT SO KANNST DU FOLGENDES TUN:\n" +
                                "1. Bestimme mithilfe eines Kartendienstes (z.B. https://www.gpskoordinaten.de/) deinen Längen- und Breitengrad.\n" +
                                "2. Setze in der Konfiguration im Abschnitt \"Benutzerdaten\" die Adresse UND Werte für \"Längengrad\" " +
                                "und \"Breitengrad\". Beachte bitte, dass du min. 4-Nachkommastellen verwendest. " +
                                "Verwendet wird das Koordinatensystem WGS 84"
                        );
                    }
                }
            }
        }

        @JsonIgnore
        public Koordinaten getCoords() throws ConfigException {
            if(Längengrad == null || Breitengrad == null){
                throw new ConfigException("Längen- oder Breitengrad fehlt");
            }
            return new Koordinaten(Breitengrad, Längengrad);
        }

        @JsonIgnore
        public boolean isComplete() {
            return Email != null &&
                    Vorname != null &&
                    Nachname != null &&
                    Adresse != null &&
                    Längengrad != null &&
                    Breitengrad != null;
        }

        @JsonIgnore
        public String getAnzeigeName() {
            if(Vorname == null & Nachname == null){
                return "Ohne Name - " + Email;
            } else if (Vorname == null) {
                return Nachname;
            } else if (Nachname == null) {
                return Vorname;
            }
            return Nachname +", " + Vorname;
        }

        @JsonIgnore
        public Schiedsrichter getSchiedsrichter() {
            return new Schiedsrichter(Vorname, Nachname);
        }

        @JsonIgnore
        public String getStrasse() {
            return Adresse.split(",")[0].trim();
        }

        @JsonIgnore
        public String getPLZOrt() {
            return Adresse.split(",")[1].trim();
        }
    }

    @ToString
    @EqualsAndHashCode
    public static class Spielablauf{
        public Integer UmziehenVorSpiel;
        public Integer EffektiveSpielDauer;
        public Integer PapierKramNachSpiel;
        public Integer UmziehenNachSpiel;

        public void updateWith(Spielablauf other) {
            if(other.UmziehenVorSpiel != null) {UmziehenVorSpiel = other.UmziehenVorSpiel;}
            if(other.EffektiveSpielDauer != null) {EffektiveSpielDauer = other.EffektiveSpielDauer;}
            if(other.PapierKramNachSpiel != null) {PapierKramNachSpiel = other.PapierKramNachSpiel;}
            if(other.UmziehenNachSpiel != null) {UmziehenNachSpiel = other.UmziehenNachSpiel;}
        }

        @JsonIgnore
        public boolean isComplete() {
            return UmziehenVorSpiel != null &&
                    EffektiveSpielDauer != null &&
                    PapierKramNachSpiel != null &&
                    UmziehenNachSpiel != null;
        }
    }

    public static SchiriConfiguration NEW_DEFAULT(String email){
        SchiriConfiguration config = new SchiriConfiguration();
        config.Benutzerdaten.Email = email;
        config.Spielablauf.UmziehenVorSpiel = 15;
        config.Spielablauf.EffektiveSpielDauer = 90;
        config.Spielablauf.PapierKramNachSpiel = 15;
        config.Spielablauf.UmziehenNachSpiel = 15;
        return config;
    }
    public void updateWith(String configUpdate, Function<String, Optional<Koordinaten>> addressToKoordinaten) throws ConfigException {
        try {
            // Remove NBSP first
            configUpdate = configUpdate.replace("\u00a0","");
            SchiriConfiguration newConfig = mapper.readValue(configUpdate, SchiriConfiguration.class);
            updateWith(newConfig, addressToKoordinaten);
        } catch (JsonProcessingException | ConfigException e) {
            throw new ConfigException("Fehler beim Aktualisieren der Konfiguration", e);
        }
    }

    public void updateWith(SchiriConfiguration other, Function<String, Optional<Koordinaten>> addressToKoordinaten) throws ConfigException {
        if(other.Benutzerdaten != null){
            Benutzerdaten.updateWith(other.Benutzerdaten, addressToKoordinaten);
        }
        if(other.Spielablauf != null){
            Spielablauf.updateWith(other.Spielablauf);
        }
        if(other.Gespannpartner != null){
            // this overrides the list, so that users can delete entries
            Gespannpartner.clear();
            other.Gespannpartner.forEach(
                    email -> Gespannpartner.add(email.toLowerCase())
            );
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
