package com.dieblich.handball.schiedsrichterassistent.mail.templates;

import com.dieblich.handball.schiedsrichterassistent.mail.Email;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

public class ErrorEmail extends Email {
    public ErrorEmail(String botEmailAddress, String schiriEmailAddress, List<Exception> exceptions) {
        super(botEmailAddress, schiriEmailAddress,
                "Fehler bei der Verarbeitung",
                """
                Hallo!
                
                Ich habe eine oder mehrere Emails von dir erhalten. Dabei sind leider Fehler aufgetreten.
                
                Da die Fehlermeldungen persönlich Daten enthalten können speichere ich sie nicht. Stattdessen füge ich
                sie an diese Email an. Entweder kannst du selbst damit etwas anfangen oder dich an den Admin wenden.
                
                === BEGIN FEHLERMELDUNGEN ===
                
                """+createExceptionReport(exceptions)+"""
                
                === ENDE FEHLERMELDUNGEN ===
                
                Viele Grüße,
                der Schiribot
                
                Eine Kreation von Martin Fritz
                """);
    }

    private static String createExceptionReport(List<Exception> exceptions) {
        StringBuilder report = new StringBuilder();
        for(Exception exception:exceptions){
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            exception.printStackTrace(pw);
            report.append(sw);
            report.append("\n\n");
        }
        return report.toString();
    }
}
