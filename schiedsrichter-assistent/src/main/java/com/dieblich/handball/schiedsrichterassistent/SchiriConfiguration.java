package com.dieblich.handball.schiedsrichterassistent;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Map;

@ToString
@EqualsAndHashCode
@SuppressWarnings({"NonAsciiCharacters", "SpellCheckingInspection"})
public class SchiriConfiguration {
    public Benutzerdaten Benutzerdaten;
    public Spielablauf Spielablauf = new Spielablauf();

    public SchiriConfiguration(@JsonProperty("Benutzerdaten.Email") String email){
        Benutzerdaten = new Benutzerdaten(email);
    }

    @ToString
    @EqualsAndHashCode
    public static class Benutzerdaten {
        public String Email;
        public String Vorname = "Max";
        public String Nachname = "Mustermann";
        public String Adresse = "Musterstr. 17, 54321 Köln";
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public Double Längengrad;
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public Double Breitengrad;
        public Benutzerdaten(@JsonProperty("Email") String email){
            this.Email = email;
        }
    }

    @ToString
    @EqualsAndHashCode
    public static class Spielablauf{
        public int UmziehenVorSpiel = 15;
        public int PapierKramNachSpiel = 15;
        public int UmziehenNachSpiel = 15;
        public TechnischeBesprechung TechnischeBesprechung = new TechnischeBesprechung();

        @ToString
        @EqualsAndHashCode
        public static class TechnischeBesprechung{
            public int StandardDauerInMinuten = 30;
            public Map<String, Integer> Abweichungen = Map.of("Regionalliga", 45, "Oberliga", 45);
        }
    }

    /// JSON serialization and deserialization
    private final static ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    public String toJSON() throws JsonProcessingException {
        return mapper.writeValueAsString(this);
    }

    public static SchiriConfiguration fromJSON(String json) throws JsonProcessingException {
        return mapper.readValue(json, SchiriConfiguration.class);
    }

}
