package com.dieblich.handball.schiedsrichterassistent.calendar;

import com.dieblich.handball.schiedsrichterassistent.SchiriConfiguration;
import lombok.Getter;

import java.time.LocalDateTime;

public class SpielAblauf{
    @Getter
    private final LocalDateTime anwurf;
    private final String ligaBezeichnungAusEmail;
    private final int fahrtzeit;
    private final SchiriConfiguration config;

    public SpielAblauf(LocalDateTime anwurf, String ligaBezeichnungAusEmail, int fahrtzeit, SchiriConfiguration config) {
        this.anwurf = anwurf;
        this.ligaBezeichnungAusEmail = ligaBezeichnungAusEmail;
        this.fahrtzeit = fahrtzeit;
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
                .minusMinutes(fahrtzeit);
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
        return getRueckfahrt().plusMinutes(fahrtzeit);
    }
}