package com.dieblich.handball.schiedsrichterassistent.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.Session;

public class WelcomeEmail extends Email{

    public static final String SUBJECT = "Willkommen beim Schiribot!";
    public WelcomeEmail(String sender, String receiver, Session session) throws MessagingException {
        super(session);
        setFrom(sender);
        setTo(receiver);
        setSubject(SUBJECT);
        setContent("""
                Hallo beim Schiribot!
                
                Grundsätzlich reicht es, wenn du mir Ansetzungsemails zuschickt - ich antworte dir dann in ca. 5 Minuten.
                
                Bevor es losgeht, brauche ich aber aber noch deine Adresse von dir. Die benötige ich, damit ich deine
                Fahrzeiten und Fahrtstrecke berechnen kann. Bitte antworte mir daher auf diese Email, wo in der ersten
                Zeile folgendes steht:
                    Adresse=Musterstraße 17, 54321 Köln
                Dabei gibst du natürlich deine richtige Adresse an.
                
                Ich speichere mir dann von dir:
                * deine Emailadresse
                * deine Adresse
                * deine Änderungen an der Konfiguration (z.B. längerer Puffer vor'm Spiel).
                
                Aktuell kannst du deine gespeicherten Daten nicht selbst löschen, bitte wende dich dafür an den
                Administrator des Schiribots.
                
                Ich speichere grundsätzlich keine Emails. ICH LÖSCHE ALLE EMAILS, die du mir zuschickst.
                Falls du mir also schon eine Ansetzungsemail zugeschickt hast, so musst du das erneut tun, nachdem du
                mir deine Adresse zugeschickt hast.
                
                Viele Grüße,
                der Schiribot
                
                Eine Kreation von Martin Fritz
                """);
    }
}
