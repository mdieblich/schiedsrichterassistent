package com.dieblich.handball.schiedsrichterassistent.calendar;

import com.dieblich.handball.schiedsrichterassistent.config.SchiriConfiguration;
import com.dieblich.handball.schiedsrichterassistent.geo.Fahrt;
import lombok.Getter;

import java.time.LocalDateTime;

public class SchirieinsatzAblauf {
    @Getter
    private final LocalDateTime anwurf;
    private final String ligaBezeichnungAusEmail;

    @Getter
    private final Fahrt fahrtZurHalle;
    @Getter
    private final Fahrt fahrtZumPartner;
    private final SchiriConfiguration config;

    public SchirieinsatzAblauf(LocalDateTime anwurf, String ligaBezeichnungAusEmail, Fahrt fahrtZurHalle, Fahrt fahrtZumPartner, SchiriConfiguration config) {
        this.anwurf = anwurf;
        this.ligaBezeichnungAusEmail = ligaBezeichnungAusEmail;
        this.fahrtZurHalle = fahrtZurHalle;
        this.fahrtZumPartner = fahrtZumPartner;
        this.config = config;
    }
    public SchirieinsatzAblauf(LocalDateTime anwurf, String ligaBezeichnungAusEmail, Fahrt fahrtZurHalle, SchiriConfiguration config) {
        this(anwurf, ligaBezeichnungAusEmail, fahrtZurHalle, null, config);
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
        if(isEinzelSchiri()){
            return getAnkunftHalle()
                    .minusMinutes(fahrtZurHalle.dauerInMinuten());
        }

        return getPartnerAbholen()
                .minusMinutes(fahrtZumPartner.dauerInMinuten());
    }

    private boolean isEinzelSchiri() {
        return fahrtZumPartner == null;
    }

    public LocalDateTime getPartnerAbholen() {
        return getAnkunftHalle()
                .minusMinutes(fahrtZurHalle.dauerInMinuten());
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
    public LocalDateTime getZurueckbringenPartner() {
        return getRueckfahrt()
                .plusMinutes(fahrtZurHalle.dauerInMinuten());
    }
    public LocalDateTime getHeimkehr() {
        if(isEinzelSchiri()) {
            return getRueckfahrt()
                    .plusMinutes(fahrtZurHalle.dauerInMinuten());
        }
        return getZurueckbringenPartner()
                .plusMinutes(fahrtZumPartner.dauerInMinuten());

    }
}