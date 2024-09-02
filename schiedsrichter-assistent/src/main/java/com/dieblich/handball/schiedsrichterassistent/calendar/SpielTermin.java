package com.dieblich.handball.schiedsrichterassistent.calendar;

import com.dieblich.handball.schiedsrichterassistent.config.ConfigException;
import com.dieblich.handball.schiedsrichterassistent.geo.GeoException;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public interface SpielTermin {
    // TODO test exceptions are thrown
    String extractCalendarEvent() throws GeoException, ConfigException;

    LocalDate getDay();

    static Date asDate(LocalDateTime localDateTime){
        Instant instant = localDateTime.atZone(ZoneId.of("Europe/Berlin")).toInstant();
        return Date.from(instant);
    }

    static String asTimeOfDay(LocalDateTime localDateTime){
        return localDateTime.format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    String getDescription() throws GeoException, ConfigException;
}
