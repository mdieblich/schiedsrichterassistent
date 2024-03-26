package com.dieblich.handball.schiedsrichterassistent;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Map;

@SuppressWarnings("NonAsciiCharacters")
public class UserConfiguration2{
    public Benutzerdaten Benutzerdaten;
    public Spielablauf Spielablauf = new Spielablauf();

    public UserConfiguration2(String email){
        Benutzerdaten = new Benutzerdaten(email);
    }

    public class Benutzerdaten {
        public String Email;
        public String Vorname = "Max";
        public String Nachname = "Mustermann";
        public String Adresse = "Musterstr. 17, 54321 Köln";
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public Double Längengrad;
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public Double Breitengrad;
        public Benutzerdaten(String email){
            this.Email = email;
        }
    }

    public class Spielablauf{
        public int UmziehenVorSpiel = 15;
        public int PapierKramNachSpiel = 15;
        public int UmziehenNachSpiel = 15;
        public TechnischeBesprechung TechnischeBesprechung = new TechnischeBesprechung();

        public class TechnischeBesprechung{
            public int StandardDauerInMinuten = 30;
            public Map<String, Integer> Abweichungen = Map.of("Regionalliga", 45, "Oberliga", 45);
        }
    }

}
