package com.dieblich.handball.schiedsrichterassistent.calendar;

import com.dieblich.handball.schiedsrichterassistent.MissingConfigException;
import com.dieblich.handball.schiedsrichterassistent.SchiriConfiguration;
import com.dieblich.handball.schiedsrichterassistent.SchiriEinsatz;
import com.dieblich.handball.schiedsrichterassistent.geo.GeoException;
import com.dieblich.handball.schiedsrichterassistent.geo.GeoServiceFake;
import com.dieblich.handball.schiedsrichterassistent.geo.Koordinaten;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class SpielTerminTest {

    @Test
    public void spielTerminHasSummary() throws GeoException, MissingConfigException {
        SpielTermin termin = prepareDefaultTermin();

        String calendarEvent = termin.extractCalendarEvent();
        assertEntryIs("SUMMARY", "Schiri: Kreisliga Herren", calendarEvent);
    }

    @SuppressWarnings("NonAsciiCharacters")
    private SpielTermin prepareDefaultTermin(){
        LocalDateTime anwurf = LocalDateTime.parse("2024-04-13T15:30:00");
        SchiriEinsatz einsatz = new SchiriEinsatz(anwurf, "Am Sportzentrum, 50259 Pulheim", "Kreisliga Herren", null, null);

        SchiriConfiguration config = SchiriConfiguration.NEW_DEFAULT("");
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
        fakeGeoService.addFahrt(coordsSchiri, coordsHalle, 30*60, 0);

        // act
        return new SpielTermin(einsatz, config, fakeGeoService);
    }

    @Test
    public void spielTerminHasLocation() throws GeoException, MissingConfigException {
        SpielTermin termin = prepareDefaultTermin();

        String calendarEvent = termin.extractCalendarEvent();
        // commas are escaped, see https://www.rfc-editor.org/rfc/rfc5545#section-3.3.11
        assertEntryIs("LOCATION", "Am Sportzentrum\\, 50259 Pulheim", calendarEvent);
    }

    @Test
    public void spielTerminStartTime() throws GeoException, MissingConfigException {
        SpielTermin termin = prepareDefaultTermin();

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
    public void spielTerminEndTime() throws GeoException, MissingConfigException {
        SpielTermin termin = prepareDefaultTermin();

        // act
        String calendarEvent = termin.extractCalendarEvent();

        // assert
        String day = "2024"+"04"+"13";
        /* 15:30 Uhr: Anwurf
         * 17:00 Uhr: Spielende, Papierkram beginnt
         * 17:15 Uhr: Umziehen
         * 17:30 Uhr: Abfahrt
         *  (30 Minuten Fahrtzeit)
         * 18:00 Uhr: Heimkehr
         * in UTC: 16:00 */
        String time = "160000";
        assertEntryIs("DTEND", day+"T"+time+"Z", calendarEvent);
    }

    private void assertEntryIs(String entry, String expected, String calendarEvent){
        Optional<String> actualValue = findValueOf(entry, calendarEvent);
        assertTrue(actualValue.isPresent(), "Entry <"+entry+"> not found in <"+calendarEvent+">");
        assertEquals(expected, actualValue.get(), "Full Event: "+ calendarEvent);
    }

    private Optional<String> findValueOf(String entry, String calendarEvent){
        String[] lines = calendarEvent.split(System.lineSeparator());
        for(String line:lines){
            if(line.startsWith(entry+":")){
                String value = line.substring(entry.length()+1);
                return Optional.of(value);
            }
        }
        return Optional.empty();
    }

}