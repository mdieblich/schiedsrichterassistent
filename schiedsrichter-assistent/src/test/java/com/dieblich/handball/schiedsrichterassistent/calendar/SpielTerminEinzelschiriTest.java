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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class SpielTerminEinzelschiriTest {

    @Test
    public void SpielTerminEinzelschiriHasSummary() throws GeoException, MissingConfigException {
        SpielTerminEinzelschiri termin = prepareDefaultTermin();

        String calendarEvent = termin.extractCalendarEvent();
        assertEntryIs("SUMMARY", "Schiri: Kreisliga Herren", calendarEvent);
    }

    @SuppressWarnings("NonAsciiCharacters")
    private SpielTerminEinzelschiri prepareDefaultTermin(){
        LocalDateTime anwurf = LocalDateTime.parse("2024-04-13T15:30:00");
        SchiriEinsatz einsatz = new SchiriEinsatz(anwurf,
                "Am Sportzentrum, 50259 Pulheim",
                "Kreisliga Herren",
                "SC Pulheim 3",
                "Fortuna Köln 4",
                new Schiedsrichter("Martin", "Witz"),
                new Schiedsrichter("Andre", "Kohlenfluss"));

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
        fakeGeoService.addFahrt(coordsSchiri, coordsHalle, 30*60, 34*1000);

        // act
        return new SpielTerminEinzelschiri(einsatz, config, fakeGeoService);
    }

    @Test
    public void SpielTerminEinzelschiriHasLocation() throws GeoException, MissingConfigException {
        SpielTerminEinzelschiri termin = prepareDefaultTermin();

        String calendarEvent = termin.extractCalendarEvent();
        // commas are escaped, see https://www.rfc-editor.org/rfc/rfc5545#section-3.3.11
        assertEntryIs("LOCATION", "Am Sportzentrum\\, 50259 Pulheim", calendarEvent);
    }

    @Test
    public void SpielTerminEinzelschiriStartTime() throws GeoException, MissingConfigException {
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
    public void SpielTerminEinzelschiriEndTime() throws GeoException, MissingConfigException {
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
    public void description() throws GeoException, MissingConfigException {
        SpielTerminEinzelschiri termin = prepareDefaultTermin();

        // act
        String calendarEvent = termin.extractCalendarEvent();

        // assert
        String beauftifulDescription = """
                SC Pulheim 3 vs. Fortuna Köln 4
                
                Berechnete Fahrtzeit: 30 Min
                Berechnete Strecke: 34 km
                
                Abfahrt:   14:15
                Ankunft:   15:00
                Anwurf:    15:30
                Spielende: 17:00
                Rückfahrt: 17:30
                Heimkehr:  18:00""";
        String expectedDescription = beauftifulDescription.replace("\n", "\\n");
        assertEntryIs("DESCRIPTION", expectedDescription, calendarEvent);
    }

    private void assertEntryIs(String entry, String expected, String calendarEvent){
        Optional<String> actualValue = findValueOf(entry, calendarEvent);
        assertTrue(actualValue.isPresent(), "Entry <"+entry+"> not found in <"+calendarEvent+">");
        assertEquals(expected, actualValue.get(), "Full Event: "+ calendarEvent);
    }

    private Optional<String> findValueOf(String keyWord, String calendarEvent){
        String[] lines = calendarEvent.split(System.lineSeparator());
        for(int i=0; i<lines.length; i++){
            String line = lines[i];
            if(line.startsWith(keyWord+":")){
                StringBuilder value = new StringBuilder(line.substring(keyWord.length() + 1));
                while(i+1<lines.length && lines[i+1].startsWith(" ")){
                    // A value con continue in the next lines
                    i++;
                    // but remove the preceding space
                    value.append(lines[i].substring(1));
                }
                return Optional.of(value.toString());
            }
        }
        return Optional.empty();
    }
}