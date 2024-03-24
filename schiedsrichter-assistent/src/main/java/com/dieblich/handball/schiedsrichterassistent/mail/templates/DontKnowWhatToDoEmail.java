package com.dieblich.handball.schiedsrichterassistent.mail.templates;

import com.dieblich.handball.schiedsrichterassistent.mail.Email;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;

public class DontKnowWhatToDoEmail extends Email{
    public static final String SUBJECT = "Unbekannte Email erhalten";
    public DontKnowWhatToDoEmail(String botEmailAddress, String schiriEmailAddress, Email unknownEmail, Session session) throws MessagingException {
        super(session);
        setFrom(botEmailAddress);
        setTo(schiriEmailAddress);
        setSubject(SUBJECT);
        setContent("""
        Hey,
        
        ich habe von dir eine Email erhalten mit dem Betreff
        """ + unknownEmail.getSubject() + """
        
        Allerdings kann ich damit gar nichts anfangen. Ich lösche sie.
        Wenn du der Meinung bist, dass das nicht so sein sollte, dann wende dich bitte an den Administrator.
               
        Viele Grüße,
        der Schiribot
        
        Eine Kreation von Martin Fritz
        """);
    }
}
