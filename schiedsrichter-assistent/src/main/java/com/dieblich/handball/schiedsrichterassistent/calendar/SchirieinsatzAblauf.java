package com.dieblich.handball.schiedsrichterassistent.calendar;

import com.dieblich.handball.schiedsrichterassistent.SchiriConfiguration;
import com.dieblich.handball.schiedsrichterassistent.geo.Fahrt;
import lombok.Getter;

import java.time.LocalDateTime;

public class SchirieinsatzAblauf {
    @Getter
    private final LocalDateTime anwurf;
    private final String ligaBezeichnungAusEmail;

    @Getter
    private final Fahrt hinfahrt;
    private final SchiriConfiguration config;

    public SchirieinsatzAblauf(LocalDateTime anwurf, String ligaBezeichnungAusEmail, Fahrt hinfahrt, SchiriConfiguration config) {
        this.anwurf = anwurf;
        this.ligaBezeichnungAusEmail = ligaBezeichnungAusEmail;
        this.hinfahrt = hinfahrt;
        this.config = config;
    }


    public LocalDateTime getTechnischBesprechung() {
        return anwurf
                .minusMinutes(config.Spielablauf.TechnischeBesprechung.getVorlaufProLiga(ligaBezeichnungAusEmail));
    }

    public LocalDateTime getAnkunftHalle() {
        return getTechnischBesprechung()
                .minusMinutes(config.Spielablauf.UmziehenVorSpiel);
    }

    public LocalDateTime getAbfahrt() {
        return getAnkunftHalle()
                .minusMinutes(hinfahrt.dauerInSekunden()/60);
    }

    public LocalDateTime getSpielEnde() {
        return getAnwurf()
                .plusMinutes(config.Spielablauf.EffektiveSpielDauer);
    }

    public LocalDateTime getRueckfahrt() {
        return getSpielEnde()
                .plusMinutes(config.Spielablauf.PapierKramNachSpiel)
                .plusMinutes(config.Spielablauf.UmziehenNachSpiel);
    }

    public LocalDateTime getHeimkehr() {
        return getRueckfahrt().plusMinutes(hinfahrt.dauerInSekunden()/60);
    }
}