package com.dieblich.handball.schiedsrichterassistent.calendar;

import com.dieblich.handball.schiedsrichterassistent.config.ConfigException;
import com.dieblich.handball.schiedsrichterassistent.Schiedsrichter;
import com.dieblich.handball.schiedsrichterassistent.config.SchiriConfiguration;
import com.dieblich.handball.schiedsrichterassistent.SchiriEinsatz;
import com.dieblich.handball.schiedsrichterassistent.geo.GeoException;
import com.dieblich.handball.schiedsrichterassistent.geo.GeoServiceFake;
import com.dieblich.handball.schiedsrichterassistent.geo.Koordinaten;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

class SpielTerminEinzelschiriTest extends SpielTerminTest{

    @Test
    public void summary() throws GeoException, ConfigException {
        SpielTerminEinzelschiri termin = prepareDefaultTermin();

        String calendarEvent = termin.extractCalendarEvent();
        assertEntryIs("SUMMARY", "Schiri: Kreisliga Herren", calendarEvent);
    }

    @SuppressWarnings("NonAsciiCharacters")
    private SpielTerminEinzelschiri prepareDefaultTermin(){
        LocalDateTime anwurf = LocalDateTime.parse("2024-04-13T15:30:00");
        SchiriEinsatz einsatz = new SchiriEinsatz("1", anwurf,
                "06017 Pulheim",
                "Am Sportzentrum",
                "50259 Pulheim",
                "Kreisliga Herren",
                "SC Pulheim 3",
                "Fortuna Köln 4",
                new Schiedsrichter("Martin", "Witz"),
                new Schiedsrichter("Andre", "Kohlenfluss"));

        SchiriConfiguration config = SchiriConfiguration.NEW_DEFAULT("");
        config.Benutzerdaten.Vorname = "Martin";
        config.Benutzerdaten.Nachname = "Witz";
        Koordinaten coordsSchiri = new Koordinaten(18.0, 17.0);
        config.Benutzerdaten.Längengrad = coordsSchiri.längengrad();
        config.Benutzerdaten.Breitengrad = coordsSchiri.breitengrad();
        config.Spielablauf.TechnischeBesprechung.StandardDauerInMinuten = 30;
        config.Spielablauf.UmziehenVorSpiel = 15;
        config.Spielablauf.EffektiveSpielDauer = 90;
        config.Spielablauf.PapierKramNachSpiel = 15;
        config.Spielablauf.UmziehenNachSpiel = 15;

        GeoServiceFake fakeGeoService = new GeoServiceFake();
        Koordinaten coordsHalle = fakeGeoService.addKoordinaten("Am Sportzentrum, 50259 Pulheim");
        fakeGeoService.addFahrt(coordsSchiri, coordsHalle, 30, 34);

        // act
        return new SpielTerminEinzelschiri(einsatz, config, fakeGeoService);
    }

    @Test
    public void location() throws GeoException, ConfigException {
        SpielTerminEinzelschiri termin = prepareDefaultTermin();

        String calendarEvent = termin.extractCalendarEvent();
        // commas are escaped, see https://www.rfc-editor.org/rfc/rfc5545#section-3.3.11
        assertEntryIs("LOCATION", "Am Sportzentrum\\, 50259 Pulheim", calendarEvent);
    }

    @Test
    public void startTime() throws GeoException, ConfigException {
        SpielTerminEinzelschiri termin = prepareDefaultTermin();

        // act
        String calendarEvent = termin.extractCalendarEvent();

        // assert
        String day = "2024"+"04"+"13";
        /* 15:30 Uhr: Anwurf
         * 15:00 Uhr: technische Besprechung
         * 14:45 Uhr: Ankunft / Umziehen
         *  (30 Minuten Fahrtzeit)
         * 14:15 Uhr: Abfahrt
         * in UTC: 12:15 */
        String time = "121500";
        assertEntryIs("DTSTART", day+"T"+time+"Z", calendarEvent);
    }
    @Test
    public void endTime() throws GeoException, ConfigException {
        SpielTerminEinzelschiri termin = prepareDefaultTermin();

        // act
        String calendarEvent = termin.extractCalendarEvent();

        // assert
        String day = "2024"+"04"+"13";
        /* 15:30 Uhr: Anwurf
         * 17:00 Uhr: Spielende, Papierkram beginnt
         * 17:15 Uhr: Umziehen
         * 17:30 Uhr: Rückfahrt
         *  (30 Minuten Fahrtzeit)
         * 18:00 Uhr: Heimkehr
         * in UTC: 16:00 */
        String time = "160000";
        assertEntryIs("DTEND", day+"T"+time+"Z", calendarEvent);
    }
    @Test
    public void description() throws GeoException, ConfigException {
        SpielTerminEinzelschiri termin = prepareDefaultTermin();

        // act
        String calendarEvent = termin.extractCalendarEvent();

        // assert
        String beauftifulDescription = """
                SC Pulheim 3 vs. Fortuna Köln 4
                
                Berechnete Fahrtzeit: 30 Min
                Berechnete Strecke: 34 km
                
                14:15 Uhr Abfahrt
                14:45 Uhr Ankunft
                15:00 Uhr Technische Besprechung
                15:30 Uhr Anwurf
                17:00 Uhr Spielende
                17:30 Uhr Rückfahrt
                18:00 Uhr Heimkehr""";
        String expectedDescription = beauftifulDescription.replace("\n", "\\n");
        assertEntryIs("DESCRIPTION", expectedDescription, calendarEvent);
    }
}