package com.dieblich.handball.schiedsrichterassistent.mail.templates;

import com.dieblich.handball.schiedsrichterassistent.MissingConfigException;
import com.dieblich.handball.schiedsrichterassistent.calendar.SpielTermin;
import com.dieblich.handball.schiedsrichterassistent.geo.GeoException;
import com.dieblich.handball.schiedsrichterassistent.mail.Email;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class CalendarResponseEmail extends Email {
    public CalendarResponseEmail(String botEmailAddress, String schiriEmailAddress, SpielTermin spielTermin, Session session) throws MessagingException, GeoException, IOException, MissingConfigException {
        super(botEmailAddress, schiriEmailAddress, session);
        setSubject("Termine für deine Ansetzung");
        setContent("Anbei deine Ansetzung.");
        File calendarInviteFile = saveToFile(spielTermin);
        attachFile(calendarInviteFile);
    }

    private File saveToFile(SpielTermin spielTermin) throws IOException, GeoException, MissingConfigException {
        long uniqueID = System.currentTimeMillis();
        String fileName = uniqueID+".ics";

        try(OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(fileName), StandardCharsets.UTF_8)){
            writer.append(spielTermin.extractCalendarEvent());
        }
        return new File(uniqueID+".ics");
    }
}
