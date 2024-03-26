package com.dieblich.handball.schiedsrichterassistent.calendar;

import com.dieblich.handball.schiedsrichterassistent.mail.UserConfiguration;

import java.time.LocalDateTime;

public class SpielAblauf{
    private LocalDateTime anwurf;
    private final String ligaBezeichnungAusEmail;
    private final int fahrtzeit;
    UserConfiguration config;

    public SpielAblauf(LocalDateTime anwurf, String ligaBezeichnungAusEmail, int fahrtzeit, UserConfiguration config) {
        this.anwurf = anwurf;
        this.ligaBezeichnungAusEmail = ligaBezeichnungAusEmail;
        this.fahrtzeit = fahrtzeit;
        this.config = config;
    }


    public LocalDateTime getAnwurf() {
        return anwurf;
    }

    public LocalDateTime getTechnischBesprechung() {
        return anwurf
                .minusMinutes(config.getTechnischeBesprechung(ligaBezeichnungAusEmail));
    }

    public LocalDateTime getAnkunft() {
        return getTechnischBesprechung();
    }
}