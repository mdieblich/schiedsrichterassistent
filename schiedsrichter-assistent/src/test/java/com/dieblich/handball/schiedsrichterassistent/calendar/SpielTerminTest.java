package com.dieblich.handball.schiedsrichterassistent.calendar;

import biweekly.ICalendar;
import com.dieblich.handball.schiedsrichterassistent.SchiriEinsatz;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SpielTerminTest {

    @Test
    public void spielTerminHasSummary(){
        SchiriEinsatz einsatz = new SchiriEinsatz(null, null, "Kreisliga Herren", null, null);
        SpielTermin termin = new SpielTermin(einsatz);

        ICalendar ical = termin.extractCalendarEvent();
        assertEquals("Schiri: Kreisliga Herren", ical.getEvents().get(0).getSummary().getValue());
    }

    @Test
    public void spielTerminHasLocation(){
        SchiriEinsatz einsatz = new SchiriEinsatz(null, "Am Sportzentrum, 50259 Pulheim", null, null, null);
        SpielTermin termin = new SpielTermin(einsatz);

        ICalendar ical = termin.extractCalendarEvent();
        assertEquals("Am Sportzentrum, 50259 Pulheim", ical.getEvents().get(0).getLocation().getValue());
    }

    @Test
    public void spielTerminStartTime(){
        SchiriEinsatz einsatz = new SchiriEinsatz(null, "Am Sportzentrum, 50259 Pulheim", null, null, null);
        SpielTermin termin = new SpielTermin(einsatz);

        ICalendar ical = termin.extractCalendarEvent();
        // TODO implement
    }

}