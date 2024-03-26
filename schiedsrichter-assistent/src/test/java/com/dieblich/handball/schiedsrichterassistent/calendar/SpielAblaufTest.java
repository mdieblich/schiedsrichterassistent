package com.dieblich.handball.schiedsrichterassistent.calendar;

import com.dieblich.handball.schiedsrichterassistent.mail.UserConfiguration;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDateTime;

import static com.dieblich.handball.schiedsrichterassistent.mail.UserConfiguration.*;
import static org.junit.jupiter.api.Assertions.*;

class SpielAblaufTest {

    @Test
    public void anwurf() throws IOException {
        UserConfiguration userConfig = new UserConfiguration(null,
                TECHNISCHE_BESPRECHUNG_ANDERE_LIGEN_DAUER_IN_MINUTEN+"=30\n");
        LocalDateTime anwurf = LocalDateTime.parse("2024-04-13T15:30:00");
        String liga = "Landesliga Männer";
        int fahrtzeit = 60; // minutes
        SpielAblauf ablauf = new SpielAblauf(anwurf, liga, fahrtzeit, userConfig);

        assertEquals(anwurf, ablauf.getAnwurf());
    }
    @Test
    public void technischeBesprechung() throws IOException {
        UserConfiguration config = new UserConfiguration("",
                TECHNISCHE_BESPRECHUNG_ANDERE_LIGEN_DAUER_IN_MINUTEN+"=30\n"
        );
        String liga = "Landesliga Männer";
        LocalDateTime anwurf = LocalDateTime.parse("2024-04-13T15:30:00");
        int fahrtzeit = 60; // minutes
        SpielAblauf ablauf = new SpielAblauf(anwurf, liga, fahrtzeit, config);

        assertEquals(LocalDateTime.parse("2024-04-13T15:00:00"), ablauf.getTechnischBesprechung());
    }
    @Test
    public void ankunft() throws IOException {
        UserConfiguration config = new UserConfiguration("",
                TECHNISCHE_BESPRECHUNG_ANDERE_LIGEN_DAUER_IN_MINUTEN+"=30\n"
        );
        String liga = "Landesliga Männer";
        LocalDateTime anwurf = LocalDateTime.parse("2024-04-13T15:30:00");
        int fahrtzeit = 60; // minutes
        SpielAblauf ablauf = new SpielAblauf(anwurf, liga, fahrtzeit, config);

        assertEquals(LocalDateTime.parse("2024-04-13T14:45:00"), ablauf.getAnkunft());
    }

}