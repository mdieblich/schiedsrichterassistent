package com.dieblich.handball.schiedsrichterassistent.mail.templates;

import com.dieblich.handball.schiedsrichterassistent.mail.Email;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;

public class WelcomeEmail extends Email {

    public static final String SUBJECT = "Willkommen beim Schiribot!";
    public WelcomeEmail(String botEmailAddress, String schiriEmailAddress, Session session) throws MessagingException {
        super(botEmailAddress, schiriEmailAddress, session);
        setSubject(SUBJECT);
        setContent("""
                Hallo beim Schiribot!
                
                Grundsätzlich reicht es, wenn du mir Ansetzungsemails zuschickt - ich antworte dir dann in ca. 5 Minuten.
                
                Bevor es losgeht, brauche ich aber aber noch deinen Namen und deine Adresse von dir. Den Namen benötige
                ich, damit ich dich in den Ansetzungs-Emails erkenne. Deine Adresse benötige ich, damit ich deine
                Fahrzeiten und Fahrtstrecke berechnen kann.
                
                Bitte schick mir daher zuerst eine Email mit dem Betreff "Konfiguration", in der folgendes steht:
                {
                    "Benutzerdaten": {
                        "Vorname": "Max",
                        "Nachname": "Mustermann",
                        "Adresse": "Musterstr. 17, 54321 Köln"
                    }
                }
                Dabei gibst du bitte deinen richtigen Namen + Adresse an.
                
                Darüber hinaus speichere ich mir dann von dir:
                * deine Emailadresse
                * deine Änderungen an der Konfiguration (z.B. längerer Puffer vor'm Spiel).
                
                Aktuell kannst du deine gespeicherten Daten nicht selbst löschen, bitte wende dich dafür an den
                Administrator des Schiribots.
                
                Ich speichere grundsätzlich keine Emails. ICH LÖSCHE ALLE EMAILS, die du mir zuschickst.
                Falls du mir also schon eine Ansetzungsemail zugeschickt hast, so musst du das erneut tun, nachdem du
                mir die obige Konfiguration zugeschickt hast.
                
                Viele Grüße,
                der Schiribot
                
                Eine Kreation von Martin Fritz
                """);
    }
}
