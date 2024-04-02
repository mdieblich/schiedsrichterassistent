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

import java.util.Optional;

public class SpielTerminFahrer implements SpielTermin{
    private final SchiriEinsatz einsatz;
    private final SchiriConfiguration schiriConfigFahrer;
    private final SchiriConfiguration schiriConfigBeifahrer;
    private final GeoService geoService;

    private SchirieinsatzAblauf spielAblauf;
    private String description;

    public SpielTerminFahrer(SchiriEinsatz einsatz, SchiriConfiguration schiriConfigFahrer, SchiriConfiguration schiriConfigBeifahrer, GeoService geoService) {
        this.einsatz = einsatz;
        this.schiriConfigFahrer = schiriConfigFahrer;
        this.schiriConfigBeifahrer = schiriConfigBeifahrer;
        this.geoService = geoService;
    }

    @Override
    public String extractCalendarEvent() throws GeoException, MissingConfigException {
        ICalendar ical = new ICalendar();
        VEvent event = new VEvent();

        event.setSummary(getSummary());
        event.setLocation(getLocation());

        SchirieinsatzAblauf ablauf = getSpielAblauf();
        event.setDateStart(SpielTermin.asDate(ablauf.getAbfahrt()));
        event.setDateEnd(SpielTermin.asDate(ablauf.getHeimkehr()));
        event.setDescription(getDescription());

        ical.addEvent(event);
        return Biweekly.write(ical).go();
    }

    public String getSummary() {
        return "Schiri: " + einsatz.ligaBezeichnungAusEmail();
    }

    public String getLocation() {
        return einsatz.hallenAdresse();
    }

    SchirieinsatzAblauf getSpielAblauf() throws GeoException, MissingConfigException {
        if(spielAblauf == null){
            spielAblauf = createSpielablauf();
        }
        return spielAblauf;
    }
    private SchirieinsatzAblauf createSpielablauf() throws MissingConfigException, GeoException {
        Koordinaten coordsFahrer = schiriConfigFahrer.Benutzerdaten.getCoords();
        Koordinaten coordsBeifahrer = schiriConfigBeifahrer.Benutzerdaten.getCoords();
        Optional<Koordinaten> optionalCoordsHalle = geoService.findKoordinaten(einsatz.hallenAdresse());
        if (optionalCoordsHalle.isEmpty()) {
            throw new GeoException("Koordinaten der Halle (" + einsatz.hallenAdresse() + ") konnten nicht bestimmt werden.");
        }
        Optional<Fahrt> optionalFahrtZumBeifahrer = geoService.calculateFahrt(coordsFahrer, coordsBeifahrer);
        if (optionalFahrtZumBeifahrer.isEmpty()) {
            throw new GeoException("Fahrt von Fahrer (" + coordsFahrer + ") zum Beifahrer (" + coordsBeifahrer + ") konnte nicht bestimmt werden.");
        }
        Optional<Fahrt> optionalFahrtZurHalle = geoService.calculateFahrt(coordsBeifahrer, optionalCoordsHalle.get());
        if (optionalFahrtZurHalle.isEmpty()) {
            throw new GeoException("Fahrt von Beifahrer (" + coordsBeifahrer + ") zur Halle (" + optionalCoordsHalle.get() + ") konnte nicht bestimmt werden.");
        }

        return new SchirieinsatzAblauf(
                einsatz.anwurf(),
                einsatz.ligaBezeichnungAusEmail(),
                optionalFahrtZurHalle.get(),
                optionalFahrtZumBeifahrer.get(),
                schiriConfigFahrer
        );
    }

    @Override
    public String getDescription() throws GeoException, MissingConfigException {
        if(description == null){
            String nameFahrer = schiriConfigFahrer.Benutzerdaten.Vorname;
            String nameBeifahrer = schiriConfigBeifahrer.Benutzerdaten.Vorname;
            SchirieinsatzAblauf ablauf = getSpielAblauf();
            description = einsatz.heimMannschaft() + " vs. " + einsatz.gastMannschaft() + "\n";
            description += "\n";
            description += "Berechnete Fahrtzeit: "+(ablauf.getFahrtZumPartner().dauerInSekunden()/60)  +" Min "+nameFahrer+" zu "+nameBeifahrer+"\n";
            description += "                      "+(ablauf.getFahrtZurHalle().dauerInSekunden()/60)    +" Min zur Halle\n";
            description += "Berechnete Strecke:   "+(ablauf.getFahrtZumPartner().distanzInMetern()/1000)+" km  "+nameFahrer+" zu "+nameBeifahrer+"\n";
            description += "                      "+(ablauf.getFahrtZurHalle().distanzInMetern()/1000)  +" km  zur Halle\n";
            description += "\n";
            description += SpielTermin.asTimeOfDay(ablauf.getAbfahrt())               + " Uhr Abfahrt " + nameFahrer                + "\n";
            description += SpielTermin.asTimeOfDay(ablauf.getPartnerAbholen())        + " Uhr " + nameBeifahrer + " abholen"        + "\n";
            description += SpielTermin.asTimeOfDay(ablauf.getTechnischBesprechung())  + " Uhr Ankunft"                              + "\n";
            description += SpielTermin.asTimeOfDay(ablauf.getAnwurf())                + " Uhr Anwurf"                               + "\n";
            description += SpielTermin.asTimeOfDay(ablauf.getSpielEnde())             + " Uhr Spielende"                            + "\n";
            description += SpielTermin.asTimeOfDay(ablauf.getRueckfahrt())            + " Uhr Rückfahrt"                            + "\n";
            description += SpielTermin.asTimeOfDay(ablauf.getZurueckbringenPartner()) + " Uhr " + nameBeifahrer + " zurückbringen"  + "\n";
            description += SpielTermin.asTimeOfDay(ablauf.getHeimkehr())              + " Uhr Heimkehr " + nameFahrer;
        }
        return description;
    }

    public SpielTerminBeifahrer createBeifahrerTermin() {
        return new SpielTerminBeifahrer(this);
    }
}
