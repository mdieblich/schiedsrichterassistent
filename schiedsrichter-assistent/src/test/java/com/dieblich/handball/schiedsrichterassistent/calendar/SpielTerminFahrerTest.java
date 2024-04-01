package com.dieblich.handball.schiedsrichterassistent.calendar;

import com.dieblich.handball.schiedsrichterassistent.MissingConfigException;
import com.dieblich.handball.schiedsrichterassistent.Schiedsrichter;
import com.dieblich.handball.schiedsrichterassistent.SchiriConfiguration;
import com.dieblich.handball.schiedsrichterassistent.SchiriEinsatz;
import com.dieblich.handball.schiedsrichterassistent.geo.GeoException;
import com.dieblich.handball.schiedsrichterassistent.geo.GeoServiceFake;
import com.dieblich.handball.schiedsrichterassistent.geo.Koordinaten;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

class SpielTerminFahrerTest extends SpielTerminTest {
    @Test
    public void summary() throws GeoException, MissingConfigException {
        SpielTerminFahrer termin = prepareDefaultTermin();

        String calendarEvent = termin.extractCalendarEvent();
        assertEntryIs("SUMMARY", "Schiri: Kreisliga Herren", calendarEvent);
    }

    @SuppressWarnings("NonAsciiCharacters")
    private SpielTerminFahrer prepareDefaultTermin(){
        LocalDateTime anwurf = LocalDateTime.parse("2024-04-13T15:30:00");
        SchiriEinsatz einsatz = new SchiriEinsatz(anwurf,
                "Am Sportzentrum, 50259 Pulheim",
                "Kreisliga Herren",
                "SC Pulheim 3",
                "Fortuna Köln 4",
                new Schiedsrichter("Martin", "Witz"),
                new Schiedsrichter("Andre", "Kohlenfluss"));

        SchiriConfiguration configFahrer = SchiriConfiguration.NEW_DEFAULT("");
        Koordinaten coordsFahrer = new Koordinaten(18.0, 17.0);
        configFahrer.Benutzerdaten.Längengrad = coordsFahrer.längengrad();
        configFahrer.Benutzerdaten.Breitengrad = coordsFahrer.breitengrad();
        configFahrer.Spielablauf.TechnischeBesprechung.StandardDauerInMinuten = 30;
        configFahrer.Spielablauf.UmziehenVorSpiel = 15;
        configFahrer.Spielablauf.EffektiveSpielDauer = 90;
        configFahrer.Spielablauf.PapierKramNachSpiel = 15;
        configFahrer.Spielablauf.UmziehenNachSpiel = 15;


        SchiriConfiguration configBeifahrer = SchiriConfiguration.NEW_DEFAULT("");
        Koordinaten coordsBeifahrer = new Koordinaten(20.0, 19.0);
        configBeifahrer.Benutzerdaten.Längengrad = coordsBeifahrer.längengrad();
        configBeifahrer.Benutzerdaten.Breitengrad = coordsBeifahrer.breitengrad();

        GeoServiceFake fakeGeoService = new GeoServiceFake();
        Koordinaten coordsHalle = fakeGeoService.addKoordinaten("Am Sportzentrum, 50259 Pulheim");
        fakeGeoService.addFahrt(coordsFahrer, coordsBeifahrer, 15*60, 10*1000);
        fakeGeoService.addFahrt(coordsBeifahrer, coordsHalle, 30*60, 34*1000);

        // act
        return new SpielTerminFahrer(einsatz, configFahrer, configBeifahrer, fakeGeoService);
    }
    @Test
    public void location() throws GeoException, MissingConfigException {
        SpielTerminFahrer termin = prepareDefaultTermin();

        String calendarEvent = termin.extractCalendarEvent();
        // commas are escaped, see https://www.rfc-editor.org/rfc/rfc5545#section-3.3.11
        assertEntryIs("LOCATION", "Am Sportzentrum\\, 50259 Pulheim", calendarEvent);
    }
    @Test
    public void startTime() throws GeoException, MissingConfigException {
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
    public void endTime() throws GeoException, MissingConfigException {
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
         * 18:15 Uhr: Zurückbringen Beifahrer
         * in UTC: 16:15 */
        String time = "161500";
        assertEntryIs("DTEND", day+"T"+time+"Z", calendarEvent);
    }

    @Test
    public void description() throws GeoException, MissingConfigException {
        SpielTerminFahrer termin = prepareDefaultTermin();

        // act
        String calendarEvent = termin.extractCalendarEvent();

        // assert
        String beauftifulDescription = """
                SC Pulheim 3 vs. Fortuna Köln 4
                
                Berechnete Fahrtzeit: 15 Min zum Partner
                                      30 Min zur Halle
                Berechnete Strecke:   10 km  zum Partner
                                      34 km  zur Halle
                
                Abfahrt:         14:00
                Partner abholen: 14:15
                Ankunft:         15:00
                Anwurf:          15:30
                Spielende:       17:00
                Rückfahrt:       17:30
                Partner zurück:  18:00
                Heimkehr:        18:15""";
        String expectedDescription = beauftifulDescription.replace("\n", "\\n");
        assertEntryIs("DESCRIPTION", expectedDescription, calendarEvent);
    }
}