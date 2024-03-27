package com.dieblich.handball.schiedsrichterassistent.calendar;

import com.dieblich.handball.schiedsrichterassistent.SchiriConfiguration;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class SpielAblaufTest {

    @Test
    public void anwurf()  {
        LocalDateTime anwurf = LocalDateTime.parse("2024-04-13T15:30:00");
        SpielAblauf ablauf = new SpielAblauf(anwurf, "", 0, null);

        assertEquals(anwurf, ablauf.getAnwurf());
    }
    @Test
    public void technischeBesprechung()  {
        SchiriConfiguration config = SchiriConfiguration.NEW_DEFAULT("");
        config.Spielablauf.TechnischeBesprechung.StandardDauerInMinuten = 30;
        String liga = "Landesliga Männer";
        LocalDateTime anwurf = LocalDateTime.parse("2024-04-13T15:30:00");
        SpielAblauf ablauf = new SpielAblauf(anwurf, liga, 0, config);

        assertEquals(LocalDateTime.parse("2024-04-13T15:00:00"), ablauf.getTechnischBesprechung());
    }
    @Test
    public void ankunft() {
        SchiriConfiguration config = SchiriConfiguration.NEW_DEFAULT("");
        config.Spielablauf.TechnischeBesprechung.StandardDauerInMinuten = 30;
        config.Spielablauf.UmziehenVorSpiel = 15;
        String liga = "Landesliga Männer";
        LocalDateTime anwurf = LocalDateTime.parse("2024-04-13T15:30:00");
        SpielAblauf ablauf = new SpielAblauf(anwurf, liga, 0, config);

        assertEquals(LocalDateTime.parse("2024-04-13T14:45:00"), ablauf.getAnkunft());
    }

}