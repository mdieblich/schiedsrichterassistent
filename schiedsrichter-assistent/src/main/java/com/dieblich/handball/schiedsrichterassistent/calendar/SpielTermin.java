package com.dieblich.handball.schiedsrichterassistent.calendar;

import com.dieblich.handball.schiedsrichterassistent.MissingConfigException;
import com.dieblich.handball.schiedsrichterassistent.geo.GeoException;

public interface SpielTermin {
    // TODO test exceptions are thrown
    String extractCalendarEvent() throws GeoException, MissingConfigException;

    String getDescription() throws GeoException, MissingConfigException;
}
