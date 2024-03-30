package com.dieblich.handball.schiedsrichterassistent.calendar;

import biweekly.Biweekly;
import biweekly.ICalendar;
import biweekly.component.VEvent;
import com.dieblich.handball.schiedsrichterassistent.MissingConfigException;
import com.dieblich.handball.schiedsrichterassistent.SchiriConfiguration;
import com.dieblich.handball.schiedsrichterassistent.SchiriEinsatz;
import com.dieblich.handball.schiedsrichterassistent.geo.Fahrt;
import com.dieblich.handball.schiedsrichterassistent.geo.GeoException;
import com.dieblich.handball.schiedsrichterassistent.geo.GeoService;
import com.dieblich.handball.schiedsrichterassistent.geo.Koordinaten;

import java.time.*;
import java.util.Date;
import java.util.Optional;

public class SpielTermin {
    private final SchiriEinsatz einsatz;
    private final SchiriConfiguration config;
    private final GeoService geoService;

    public SpielTermin(SchiriEinsatz einsatz, SchiriConfiguration config, GeoService geoService) {
        this.einsatz = einsatz;
        this.config = config;
        this.geoService = geoService;
    }

    // TODO test exceptions are thrown
    public String extractCalendarEvent() throws GeoException, MissingConfigException {
        ICalendar ical = new ICalendar();
        VEvent event = new VEvent();

        event.setSummary("Schiri: " + einsatz.ligaBezeichnungAusEmail());
        event.setLocation(einsatz.hallenAdresse());

        SpielAblauf ablauf = createSpielablauf();
        event.setDateStart(asDate(ablauf.getAbfahrt()));
        event.setDateEnd(asDate(ablauf.getHeimkehr()));

        ical.addEvent(event);
        return Biweekly.write(ical).go();
    }

    private static Date asDate(LocalDateTime localDateTime){
        Instant instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
        return Date.from(instant);
    }

    private SpielAblauf createSpielablauf() throws MissingConfigException, GeoException {
        Koordinaten coordsSchiri = config.Benutzerdaten.getCoords();
        Optional<Koordinaten> optionalCoordsHalle = geoService.findKoordinaten(einsatz.hallenAdresse());
        if(optionalCoordsHalle.isEmpty()){
            throw new GeoException("Koordinaten der Halle ("+einsatz.hallenAdresse()+") konnten nicht bestimmt werden.");
        }
        Optional<Fahrt> optionalHinfahrt = geoService.calculateFahrt(coordsSchiri, optionalCoordsHalle.get());
        if(optionalHinfahrt.isEmpty()){
            throw new GeoException("Fahrt von Schiri ("+coordsSchiri+") zur Halle ("+optionalCoordsHalle.get()+") konnte nicht bestimmt werden.");
        }

        return new SpielAblauf(
                einsatz.anwurf(),
                einsatz.ligaBezeichnungAusEmail(),
                optionalHinfahrt.get().dauerInSekunden()/60,
                config
        );
    }
}
