package com.dieblich.handball.schiedsrichterassistent.mail.templates;

import com.dieblich.handball.schiedsrichterassistent.mail.Email;

public class DontKnowWhatToDoEmail extends Email{
    public DontKnowWhatToDoEmail(String botEmailAddress, String schiriEmailAddress, Email unknownEmail) {
        super(botEmailAddress, schiriEmailAddress,
    "Unbekannte Email erhalten","""
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
