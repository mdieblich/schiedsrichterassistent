package com.dieblich.handball.schiedsrichterassistent.calendar;

import biweekly.Biweekly;
import biweekly.ICalendar;
import biweekly.component.VEvent;
import com.dieblich.handball.schiedsrichterassistent.config.ConfigException;
import com.dieblich.handball.schiedsrichterassistent.config.SchiriConfiguration;
import com.dieblich.handball.schiedsrichterassistent.SchiriEinsatz;
import com.dieblich.handball.schiedsrichterassistent.geo.Fahrt;
import com.dieblich.handball.schiedsrichterassistent.geo.GeoException;
import com.dieblich.handball.schiedsrichterassistent.geo.GeoService;
import com.dieblich.handball.schiedsrichterassistent.geo.Koordinaten;

import java.time.LocalDate;
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

    @Override
    public LocalDate getDay() {
        return einsatz.anwurf().toLocalDate();
    }

    // TODO test exceptions are thrown
    @Override
    public String extractCalendarEvent() throws GeoException, ConfigException {
        ICalendar ical = new ICalendar();
        VEvent event = new VEvent();

        event.setSummary("Schiri: " + einsatz.ligaBezeichnungAusEmail());
        event.setLocation(einsatz.hallenAdresse());

        SchirieinsatzAblauf ablauf = getSpielAblauf();
        event.setDateStart(SpielTermin.asDate(ablauf.getAbfahrt()));
        event.setDateEnd(SpielTermin.asDate(ablauf.getHeimkehr()));
        event.setDescription(getDescription());

        ical.addEvent(event);
        return Biweekly.write(ical).go();
    }

    public SchirieinsatzAblauf getSpielAblauf() throws GeoException, ConfigException {
        if(spielAblauf == null){
            spielAblauf = createSpielablauf();
        }
        return spielAblauf;
    }

    private SchirieinsatzAblauf createSpielablauf() throws ConfigException, GeoException {
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
                optionalHinfahrt.get(),
                config
        );
    }

    @Override
    public String getDescription() throws GeoException, ConfigException {
        if(description == null){
            SchirieinsatzAblauf ablauf = getSpielAblauf();
            description = einsatz.heimMannschaft() + " vs. " + einsatz.gastMannschaft() + "\n";
            description += "\n";
            description += "Berechnete Fahrtzeit: "+ ablauf.getFahrtZurHalle().dauerInMinuten()      + " Min\n";
            description += "Berechnete Strecke: "  + ablauf.getFahrtZurHalle().distanzInKilometern() + " km\n";
            description += "\n";
            description += SpielTermin.asTimeOfDay(ablauf.getAbfahrt())              + " Uhr Abfahrt" + "\n";
            description += SpielTermin.asTimeOfDay(ablauf.getAnkunftHalle())         + " Uhr Ankunft" + "\n";
            description += SpielTermin.asTimeOfDay(ablauf.getTechnischBesprechung()) + " Uhr Technische Besprechung" + "\n";
            description += SpielTermin.asTimeOfDay(ablauf.getAnwurf())               + " Uhr Anwurf" + "\n";
            description += SpielTermin.asTimeOfDay(ablauf.getSpielEnde())            + " Uhr Spielende" + "\n";
            description += SpielTermin.asTimeOfDay(ablauf.getRueckfahrt())           + " Uhr Rückfahrt" + "\n";
            description += SpielTermin.asTimeOfDay(ablauf.getHeimkehr())             + " Uhr Heimkehr";
        }
        return description;
    }
}
