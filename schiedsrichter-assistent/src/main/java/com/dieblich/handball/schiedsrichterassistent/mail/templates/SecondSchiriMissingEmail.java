package com.dieblich.handball.schiedsrichterassistent.mail.templates;

import com.dieblich.handball.schiedsrichterassistent.Schiedsrichter;
import com.dieblich.handball.schiedsrichterassistent.mail.Email;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;

public class SecondSchiriMissingEmail extends Email {

    public SecondSchiriMissingEmail(String botEmailAddress, String schiriEmailAddress, Schiedsrichter otherSchiri, Session session) throws MessagingException {
        super(botEmailAddress, schiriEmailAddress, session);
        setSubject("Partner-Konfiguration fehlt.");
        setContent("""
                Hallo!
                
                Ich habe eine Ansetzungsemail von dir bekommen.
                In dieser wird jedoch \""""+ otherSchiri.fullName() + "\""+"""
                erwähnt, aber ich finde keine Konfiguration dieser Person.
                
                Bitte stellt sicher, dass \"""" + otherSchiri.fullName() + "\" "+ """
                sich auch beim Schiribot registriert hat und exakt den Namen verwendet, wie er hier angegeben ist
                (das betrifft Akzentzeichen, Bindestriche, weiterer Vornamen, etc.).
                
                Die Ansetzungsemail habe ich ignoriert & gelöscht.
                
                Viele Grüße,
                der Schiribot
                """);
    }
}
