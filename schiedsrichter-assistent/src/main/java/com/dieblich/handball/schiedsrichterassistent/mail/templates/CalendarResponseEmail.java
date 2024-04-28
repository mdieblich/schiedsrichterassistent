package com.dieblich.handball.schiedsrichterassistent.mail.templates;

import com.dieblich.handball.schiedsrichterassistent.ConfigException;
import com.dieblich.handball.schiedsrichterassistent.calendar.SpielTermin;
import com.dieblich.handball.schiedsrichterassistent.geo.GeoException;
import com.dieblich.handball.schiedsrichterassistent.mail.Email;
import com.dieblich.handball.schiedsrichterassistent.pdf.Kostenabrechnung;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;

public class CalendarResponseEmail extends Email implements AutoCloseable{

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public CalendarResponseEmail(String botEmailAddress, String schiriEmailAddress, SpielTermin spielTermin, Kostenabrechnung kostenabrechnung) throws GeoException, IOException, ConfigException {
        super(botEmailAddress, schiriEmailAddress,
                "Termine für deine Ansetzung am "+spielTermin.getDay().format(DATE_FORMAT),
                "Anbei deine Ansetzung.\n\n" + spielTermin.getDescription(),
                saveToFile(spielTermin),
                saveToFile(kostenabrechnung)
        );
    }

    private static File saveToFile(SpielTermin spielTermin) throws IOException, GeoException, ConfigException {
        long uniqueID = System.currentTimeMillis();
        String fileName = uniqueID+".ics";

        try(OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(fileName), StandardCharsets.UTF_8)){
            writer.append(spielTermin.extractCalendarEvent());
        }
        return new File(uniqueID+".ics");
    }

    private static File saveToFile(Kostenabrechnung kostenabrechnung) throws IOException {
        long uniqueID = System.currentTimeMillis();
        String fileName = uniqueID+".pdf";

        kostenabrechnung.exportToPDF(fileName);
        return new File(fileName);
    }


    @Override
    public void close() {
        for(File attachment:getAttachments()){
            //noinspection ResultOfMethodCallIgnored
            attachment.delete();
        }
    }
}
