package com.dieblich.handball.schiedsrichterassistent.calendar;

import com.dieblich.handball.schiedsrichterassistent.config.ConfigException;
import com.dieblich.handball.schiedsrichterassistent.Schiedsrichter;
import com.dieblich.handball.schiedsrichterassistent.config.SchiriConfiguration;
import com.dieblich.handball.schiedsrichterassistent.SchiriEinsatz;
import com.dieblich.handball.schiedsrichterassistent.config.TechnischeBesprechungConfiguration;
import com.dieblich.handball.schiedsrichterassistent.geo.GeoException;
import com.dieblich.handball.schiedsrichterassistent.geo.GeoServiceFake;
import com.dieblich.handball.schiedsrichterassistent.geo.Koordinaten;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

class SpielTerminFahrerTest extends SpielTerminTest {
    @Test
    public void summary() throws GeoException, ConfigException {
        SpielTerminFahrer termin = prepareDefaultTermin();

        String calendarEvent = termin.extractCalendarEvent();
        assertEntryIs("SUMMARY", "Schiri: Kreisliga Herren", calendarEvent);
    }

    @SuppressWarnings("NonAsciiCharacters")
    private SpielTerminFahrer prepareDefaultTermin(){
        LocalDateTime anwurf = LocalDateTime.parse("2024-04-13T15:30:00");
        SchiriEinsatz einsatz = new SchiriEinsatz("1", anwurf,
                "0617 Pulheim",
                "Am Sportzentrum",
                "50259 Pulheim",
                "Kreisliga Herren",
                "SC Pulheim 3",
                "Fortuna Köln 4",
                new Schiedsrichter("Martin", "Witz"),
                new Schiedsrichter("Andre", "Kohlenfluss"));

        SchiriConfiguration configFahrer = SchiriConfiguration.NEW_DEFAULT("");
        configFahrer.Benutzerdaten.Vorname = "Martin";
        configFahrer.Benutzerdaten.Nachname = "Witz";
        Koordinaten coordsFahrer = new Koordinaten(18.0, 17.0);
        configFahrer.Benutzerdaten.Längengrad = coordsFahrer.längengrad();
        configFahrer.Benutzerdaten.Breitengrad = coordsFahrer.breitengrad();
        configFahrer.Spielablauf.UmziehenVorSpiel = 15;
        configFahrer.Spielablauf.EffektiveSpielDauer = 90;
        configFahrer.Spielablauf.PapierKramNachSpiel = 15;
        configFahrer.Spielablauf.UmziehenNachSpiel = 15;


        SchiriConfiguration configBeifahrer = SchiriConfiguration.NEW_DEFAULT("");
        configBeifahrer.Benutzerdaten.Vorname = "Andre";
        configBeifahrer.Benutzerdaten.Nachname = "Kohlenfluss";
        Koordinaten coordsBeifahrer = new Koordinaten(20.0, 19.0);
        configBeifahrer.Benutzerdaten.Längengrad = coordsBeifahrer.längengrad();
        configBeifahrer.Benutzerdaten.Breitengrad = coordsBeifahrer.breitengrad();

        TechnischeBesprechungConfiguration technischeBesprechungConfiguration = TechnischeBesprechungConfiguration.defaultConfig();
        technischeBesprechungConfiguration.standard = 30;

        GeoServiceFake fakeGeoService = new GeoServiceFake();
        Koordinaten coordsHalle = fakeGeoService.addKoordinaten("Am Sportzentrum, 50259 Pulheim");
        fakeGeoService.addFahrt(coordsFahrer, coordsBeifahrer, 15, 10);
        fakeGeoService.addFahrt(coordsBeifahrer, coordsHalle, 30, 34);

        // act
        return new SpielTerminFahrer(einsatz, configFahrer, configBeifahrer, technischeBesprechungConfiguration, fakeGeoService);
    }
    @Test
    public void location() throws GeoException, ConfigException {
        SpielTerminFahrer termin = prepareDefaultTermin();

        String calendarEvent = termin.extractCalendarEvent();
        // commas are escaped, see https://www.rfc-editor.org/rfc/rfc5545#section-3.3.11
        assertEntryIs("LOCATION", "Am Sportzentrum\\, 50259 Pulheim", calendarEvent);
    }
    @Test
    public void startTime() throws GeoException, ConfigException {
        SpielTerminFahrer termin = prepareDefaultTermin();

        // act
        String calendarEvent = termin.extractCalendarEvent();

        // assert
        String day = "2024"+"04"+"13";
        /* 15:30 Uhr: Anwurf
         * 15:00 Uhr: technische Besprechung
         * 14:45 Uhr: Ankunft / Umziehen
         *  (30 Minuten Fahrtzeit von Beifahrer zur Halle)
         * 14:15 Uhr: Abholen Beifahrer
         *  (15 Minuten Fahrtzeit zum Beifahrer)
         * 14:00 Uhr: Abfahrt
         * in UTC: 12:00 */
        String time = "120000";
        assertEntryIs("DTSTART", day+"T"+time+"Z", calendarEvent);
    }

    @Test
    public void endTime() throws GeoException, ConfigException {
        SpielTerminFahrer termin = prepareDefaultTermin();

        // act
        String calendarEvent = termin.extractCalendarEvent();

        // assert
        String day = "2024"+"04"+"13";
        /* 15:30 Uhr: Anwurf
         * 17:00 Uhr: Spielende, Papierkram beginnt
         * 17:15 Uhr: Umziehen
         * 17:30 Uhr: Rückfahrt
         *  (30 Minuten Fahrtzeit von Halle zum Beifahrer)
         * 18:00 Uhr: Zurückbringen Beifahrer
         *  (15 Minuten Fahrtzeit vom Beifahrer nach Hause)
         * 18:15 Uhr: Rückkehr
         * in UTC: 16:15 */
        String time = "161500";
        assertEntryIs("DTEND", day+"T"+time+"Z", calendarEvent);
    }

    @Test
    public void description() throws GeoException, ConfigException {
        SpielTerminFahrer termin = prepareDefaultTermin();

        // act
        String calendarEvent = termin.extractCalendarEvent();

        // assert
        String beautifulDescription = """
                SC Pulheim 3 vs. Fortuna Köln 4
                
                Berechnete Fahrtzeit: 15 Min Martin zu Andre
                                      30 Min zur Halle
                Berechnete Strecke:   10 km  Martin zu Andre
                                      34 km  zur Halle
                
                14:00 Uhr Abfahrt Martin
                14:15 Uhr Andre abholen
                14:45 Uhr Ankunft
                15:00 Uhr Technische Besprechung
                15:30 Uhr Anwurf
                17:00 Uhr Spielende
                17:30 Uhr Rückfahrt
                18:00 Uhr Andre zurückbringen
                18:15 Uhr Heimkehr Martin""";
        String expectedDescription = beautifulDescription.replace("\n", "\\n");
        assertEntryIs("DESCRIPTION", expectedDescription, calendarEvent);
    }
}