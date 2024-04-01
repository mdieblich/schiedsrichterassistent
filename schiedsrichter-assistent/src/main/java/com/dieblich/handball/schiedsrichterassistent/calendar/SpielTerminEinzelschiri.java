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
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Optional;

public class SpielTerminEinzelschiri implements SpielTermin {

    private final SchiriEinsatz einsatz;
    private final SchiriConfiguration config;
    private final GeoService geoService;

    private SchirieinsatzAblauf spielAblauf;
    private String description;

    public SpielTerminEinzelschiri(SchiriEinsatz einsatz, SchiriConfiguration config, GeoService geoService) {
        this.einsatz = einsatz;
        this.config = config;
        this.geoService = geoService;
    }

    // TODO test exceptions are thrown
    @Override
    public String extractCalendarEvent() throws GeoException, MissingConfigException {
        ICalendar ical = new ICalendar();
        VEvent event = new VEvent();

        event.setSummary("Schiri: " + einsatz.ligaBezeichnungAusEmail());
        event.setLocation(einsatz.hallenAdresse());

        SchirieinsatzAblauf ablauf = getSpielAblauf();
        event.setDateStart(asDate(ablauf.getAbfahrt()));
        event.setDateEnd(asDate(ablauf.getHeimkehr()));
        event.setDescription(getDescription());

        ical.addEvent(event);
        return Biweekly.write(ical).go();
    }

    private static Date asDate(LocalDateTime localDateTime){
        Instant instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
        return Date.from(instant);
    }

    private static String asTimeOfDay(LocalDateTime localDateTime){
        return localDateTime.format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    public SchirieinsatzAblauf getSpielAblauf() throws GeoException, MissingConfigException {
        if(spielAblauf == null){
            spielAblauf = createSpielablauf();
        }
        return spielAblauf;
    }

    private SchirieinsatzAblauf createSpielablauf() throws MissingConfigException, GeoException {
        Koordinaten coordsSchiri = config.Benutzerdaten.getCoords();
        Optional<Koordinaten> optionalCoordsHalle = geoService.findKoordinaten(einsatz.hallenAdresse());
        if (optionalCoordsHalle.isEmpty()) {
            throw new GeoException("Koordinaten der Halle (" + einsatz.hallenAdresse() + ") konnten nicht bestimmt werden.");
        }
        Optional<Fahrt> optionalHinfahrt = geoService.calculateFahrt(coordsSchiri, optionalCoordsHalle.get());
        if (optionalHinfahrt.isEmpty()) {
            throw new GeoException("Fahrt von Schiri (" + coordsSchiri + ") zur Halle (" + optionalCoordsHalle.get() + ") konnte nicht bestimmt werden.");
        }

        return new SchirieinsatzAblauf(
                einsatz.anwurf(),
                einsatz.ligaBezeichnungAusEmail(),
                optionalHinfahrt.get().dauerInSekunden() / 60,
                optionalHinfahrt.get().distanzInMetern()/1000,
                config
        );
    }

    @Override
    public String getDescription() throws GeoException, MissingConfigException {
        if(description == null){
            SchirieinsatzAblauf ablauf = getSpielAblauf();
            description = einsatz.heimMannschaft() + " vs. " + einsatz.gastMannschaft() + "\n";
            description += "\n";
            description += "Berechnete Fahrtzeit: "+ablauf.getFahrtzeit()+" Min\n";
            description += "Berechnete Strecke: "+ablauf.getDistanz()+" km\n";
            description += "\n";
            description += "Abfahrt:   " + asTimeOfDay(ablauf.getAbfahrt()) + "\n";
            description += "Ankunft:   " + asTimeOfDay(ablauf.getTechnischBesprechung()) + "\n";
            description += "Anwurf:    " + asTimeOfDay(ablauf.getAnwurf()) + "\n";
            description += "Spielende: " + asTimeOfDay(ablauf.getSpielEnde()) + "\n";
            description += "Rückfahrt: " + asTimeOfDay(ablauf.getRueckfahrt()) + "\n";
            description += "Heimkehr:  " + asTimeOfDay(ablauf.getHeimkehr());
        }
        return description;
    }
}
