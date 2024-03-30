package com.dieblich.handball.schiedsrichterassistent.calendar;

import com.dieblich.handball.schiedsrichterassistent.SchiriConfiguration;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class SchirieinsatzAblaufTest {

    @Test
    public void anwurf()  {
        LocalDateTime anwurf = LocalDateTime.parse("2024-04-13T15:30:00");
        SchirieinsatzAblauf ablauf = new SchirieinsatzAblauf(anwurf, "", 0, null);

        assertEquals(anwurf, ablauf.getAnwurf());
    }
    @Test
    public void technischeBesprechung()  {
        SchiriConfiguration config = SchiriConfiguration.NEW_DEFAULT("");
        config.Spielablauf.TechnischeBesprechung.StandardDauerInMinuten = 30;
        String liga = "Landesliga Männer";
        LocalDateTime anwurf = LocalDateTime.parse("2024-04-13T15:30:00");
        SchirieinsatzAblauf ablauf = new SchirieinsatzAblauf(anwurf, liga, 0, config);

        assertEquals(LocalDateTime.parse("2024-04-13T15:00:00"), ablauf.getTechnischBesprechung());
    }
    @Test
    public void ankunftHalle() {
        SchiriConfiguration config = SchiriConfiguration.NEW_DEFAULT("");
        config.Spielablauf.TechnischeBesprechung.StandardDauerInMinuten = 30;
        config.Spielablauf.UmziehenVorSpiel = 15;
        String liga = "Landesliga Männer";
        LocalDateTime anwurf = LocalDateTime.parse("2024-04-13T15:30:00");
        SchirieinsatzAblauf ablauf = new SchirieinsatzAblauf(anwurf, liga, 0, config);

        assertEquals(LocalDateTime.parse("2024-04-13T14:45:00"), ablauf.getAnkunftHalle());
    }

    @Test
    public void abfahrt() {
        SchiriConfiguration config = SchiriConfiguration.NEW_DEFAULT("");
        config.Spielablauf.TechnischeBesprechung.StandardDauerInMinuten = 30;
        config.Spielablauf.UmziehenVorSpiel = 15;
        String liga = "Landesliga Männer";
        LocalDateTime anwurf = LocalDateTime.parse("2024-04-13T15:30:00");
        SchirieinsatzAblauf ablauf = new SchirieinsatzAblauf(anwurf, liga, 45, config);

        assertEquals(LocalDateTime.parse("2024-04-13T14:00:00"), ablauf.getAbfahrt());
    }

    @Test
    public void spielEnde() {
        SchiriConfiguration config = SchiriConfiguration.NEW_DEFAULT("");
        config.Spielablauf.EffektiveSpielDauer = 90;
        LocalDateTime anwurf = LocalDateTime.parse("2024-04-13T15:30:00");
        SchirieinsatzAblauf ablauf = new SchirieinsatzAblauf(anwurf, "", 0, config);

        assertEquals(LocalDateTime.parse("2024-04-13T17:00:00"), ablauf.getSpielEnde());
    }

    @Test
    public void rueckfahrt() {
        SchiriConfiguration config = SchiriConfiguration.NEW_DEFAULT("");
        config.Spielablauf.EffektiveSpielDauer = 90;
        config.Spielablauf.PapierKramNachSpiel = 15;
        config.Spielablauf.UmziehenNachSpiel = 15;
        LocalDateTime anwurf = LocalDateTime.parse("2024-04-13T15:30:00");
        SchirieinsatzAblauf ablauf = new SchirieinsatzAblauf(anwurf, "", 0, config);

        assertEquals(LocalDateTime.parse("2024-04-13T17:30:00"), ablauf.getRueckfahrt());
    }

    @Test
    public void heimkehr() {
        SchiriConfiguration config = SchiriConfiguration.NEW_DEFAULT("");
        config.Spielablauf.EffektiveSpielDauer = 90;
        config.Spielablauf.PapierKramNachSpiel = 15;
        config.Spielablauf.UmziehenNachSpiel = 15;
        LocalDateTime anwurf = LocalDateTime.parse("2024-04-13T15:30:00");
        SchirieinsatzAblauf ablauf = new SchirieinsatzAblauf(anwurf, "", 45, config);

        assertEquals(LocalDateTime.parse("2024-04-13T18:15:00"), ablauf.getHeimkehr());
    }

}