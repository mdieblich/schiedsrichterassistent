package com.dieblich.handball.schiedsrichterassistent.mail.templates;

import com.dieblich.handball.schiedsrichterassistent.Schiedsrichter;
import com.dieblich.handball.schiedsrichterassistent.mail.Email;

public class YouAreNotWhitelistedEmail extends Email {
    public YouAreNotWhitelistedEmail(String botEmailAddress, String schiriEmailAddress, Schiedsrichter otherSchiri){
        super(botEmailAddress, schiriEmailAddress,
        "Partner hat dich nicht autorisiert", """
                Hallo!
                
                Ich habe eine Ansetzungsemail von dir bekommen.
                In dieser wird \""""+ otherSchiri.fullName() + "\""+"""
                erwähnt, aber deine Gespannpartnerin / dein Gespannpartner hat dich nicht in der Gespannpartnerliste.
                Ich schicke auch eine Email an sie/ihn, damit er/sie dich dort aufnimmt.
                
                Die Ansetzungsemail habe ich ignoriert & gelöscht.
                
                Viele Grüße,
                der Schiribot
                """);
    }

}
