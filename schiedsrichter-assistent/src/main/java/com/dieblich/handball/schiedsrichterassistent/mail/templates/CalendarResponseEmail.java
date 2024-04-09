package com.dieblich.handball.schiedsrichterassistent.mail.templates;

import com.dieblich.handball.schiedsrichterassistent.MissingConfigException;
import com.dieblich.handball.schiedsrichterassistent.calendar.SpielTermin;
import com.dieblich.handball.schiedsrichterassistent.geo.GeoException;
import com.dieblich.handball.schiedsrichterassistent.mail.Email;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class CalendarResponseEmail extends Email implements AutoCloseable{

    public CalendarResponseEmail(String botEmailAddress, String schiriEmailAddress, SpielTermin spielTermin) throws GeoException, IOException, MissingConfigException {
        super(botEmailAddress, schiriEmailAddress,
                "Termine für deine Ansetzung",
                "Anbei deine Ansetzung.\n\n" + spielTermin.getDescription(),
                saveToFile(spielTermin)
        );
    }

    private static File saveToFile(SpielTermin spielTermin) throws IOException, GeoException, MissingConfigException {
        long uniqueID = System.currentTimeMillis();
        String fileName = uniqueID+".ics";

        try(OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(fileName), StandardCharsets.UTF_8)){
            writer.append(spielTermin.extractCalendarEvent());
        }
        return new File(uniqueID+".ics");
    }

    @Override
    public void close() {
        //noinspection ResultOfMethodCallIgnored
        getAttachment().delete();
    }
}
