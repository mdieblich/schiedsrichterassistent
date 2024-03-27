package com.dieblich.handball.schiedsrichterassistent.calendar;

import biweekly.ICalendar;
import biweekly.component.VEvent;
import com.dieblich.handball.schiedsrichterassistent.SchiriEinsatz;

public class SpielTermin {
    private final SchiriEinsatz einsatz;

    public SpielTermin(SchiriEinsatz einsatz) {
        this.einsatz = einsatz;
    }

    public ICalendar extractCalendarEvent() {
        ICalendar ical = new  ICalendar();
        VEvent event = new VEvent();

        event.setSummary("Schiri: " + einsatz.ligaBezeichnungAusEmail());
        event.setLocation(einsatz.hallenAdresse());

        ical.addEvent(event);
        return ical;
    }
}
