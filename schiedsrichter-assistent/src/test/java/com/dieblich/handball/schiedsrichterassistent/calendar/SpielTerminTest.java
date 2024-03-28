package com.dieblich.handball.schiedsrichterassistent.calendar;

import biweekly.ICalendar;
import com.dieblich.handball.schiedsrichterassistent.SchiriEinsatz;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class SpielTerminTest {

    @Test
    public void spielTerminHasSummary(){
        SchiriEinsatz einsatz = new SchiriEinsatz(null, null, "Kreisliga Herren", null, null);
        SpielTermin termin = new SpielTermin(einsatz, null, null);

        ICalendar ical = termin.extractCalendarEvent();
        assertEquals("Schiri: Kreisliga Herren", ical.getEvents().get(0).getSummary().getValue());
    }

    @Test
    public void spielTerminHasLocation(){
        SchiriEinsatz einsatz = new SchiriEinsatz(null, "Am Sportzentrum, 50259 Pulheim", null, null, null);
        SpielTermin termin = new SpielTermin(einsatz, null, null);

        ICalendar ical = termin.extractCalendarEvent();
        assertEquals("Am Sportzentrum, 50259 Pulheim", ical.getEvents().get(0).getLocation().getValue());
    }

    @Test
    public void spielTerminStartTime(){
        LocalDateTime anwurf = LocalDateTime.parse("2024-04-13T15:30:00");
        SchiriEinsatz einsatz = new SchiriEinsatz(anwurf, "Am Sportzentrum, 50259 Pulheim", null, null, null);
        SpielTermin termin = new SpielTermin(einsatz, null, null);

        ICalendar ical = termin.extractCalendarEvent();
        // TODO implement
    }

}