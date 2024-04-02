package com.dieblich.handball.schiedsrichterassistent.calendar;

import biweekly.Biweekly;
import biweekly.ICalendar;
import biweekly.component.VEvent;
import com.dieblich.handball.schiedsrichterassistent.MissingConfigException;
import com.dieblich.handball.schiedsrichterassistent.geo.GeoException;

public class SpielTerminBeifahrer implements SpielTermin{
    private final SpielTerminFahrer spielTerminFahrer;

    public SpielTerminBeifahrer(SpielTerminFahrer spielTerminFahrer) {
        this.spielTerminFahrer = spielTerminFahrer;
    }

    @Override
    public String extractCalendarEvent() throws GeoException, MissingConfigException {
        ICalendar ical = new ICalendar();
        VEvent event = new VEvent();

        event.setSummary(spielTerminFahrer.getSummary());
        event.setLocation(spielTerminFahrer.getLocation());

        SchirieinsatzAblauf ablauf = spielTerminFahrer.getSpielAblauf();
        event.setDateStart(SpielTermin.asDate(ablauf.getPartnerAbholen()));
        event.setDateEnd(SpielTermin.asDate(ablauf.getZurueckbringenPartner()));
        event.setDescription(getDescription());

        ical.addEvent(event);
        return Biweekly.write(ical).go();
    }

    @Override
    public String getDescription() throws GeoException, MissingConfigException {
        return spielTerminFahrer.getDescription();
    }
}
