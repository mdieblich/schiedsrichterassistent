package com.dieblich.handball.schiedsrichterassistent.calendar;

import com.dieblich.handball.schiedsrichterassistent.config.SchiriConfiguration;
import com.dieblich.handball.schiedsrichterassistent.config.TechnischeBesprechungConfiguration;
import com.dieblich.handball.schiedsrichterassistent.geo.Fahrt;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("NonAsciiCharacters")
class SchirieinsatzAblaufTest {

    @Test
    public void anwurf()  {
        LocalDateTime anwurf = LocalDateTime.parse("2024-04-13T15:30:00");
        SchirieinsatzAblauf ablauf = new SchirieinsatzAblauf(anwurf, "", null, null, null);

        assertEquals(anwurf, ablauf.getAnwurf());
    }
    @Test
    public void technischeBesprechung()  {
        String liga = "Landesliga Männer";
        LocalDateTime anwurf = LocalDateTime.parse("2024-04-13T15:30:00");
        SchirieinsatzAblauf ablauf = new SchirieinsatzAblauf(anwurf, liga, null, null, technBespr30Min());

        assertEquals(LocalDateTime.parse("2024-04-13T15:00:00"), ablauf.getTechnischBesprechung());
    }

    private TechnischeBesprechungConfiguration technBespr30Min(){
        TechnischeBesprechungConfiguration technischeBesprechungConfiguration = TechnischeBesprechungConfiguration.defaultConfig();
        technischeBesprechungConfiguration.standard = 30;
        return technischeBesprechungConfiguration;
    }

    @Test
    public void ankunftHalle() {
        SchiriConfiguration config = SchiriConfiguration.NEW_DEFAULT("");
        config.Spielablauf.UmziehenVorSpiel = 15;
        String liga = "Landesliga Männer";
        LocalDateTime anwurf = LocalDateTime.parse("2024-04-13T15:30:00");
        SchirieinsatzAblauf ablauf = new SchirieinsatzAblauf(anwurf, liga, null, config, technBespr30Min());

        assertEquals(LocalDateTime.parse("2024-04-13T14:45:00"), ablauf.getAnkunftHalle());
    }

    @Test
    public void abfahrtBeiEinemSchiri() {
        SchiriConfiguration config = SchiriConfiguration.NEW_DEFAULT("");
        config.Spielablauf.UmziehenVorSpiel = 15;
        String liga = "Landesliga Männer";
        LocalDateTime anwurf = LocalDateTime.parse("2024-04-13T15:30:00");
        Fahrt zurHalle = new Fahrt(45, 0);
        SchirieinsatzAblauf ablauf = new SchirieinsatzAblauf(anwurf, liga, zurHalle, config, technBespr30Min());

        assertEquals(LocalDateTime.parse("2024-04-13T14:00:00"), ablauf.getAbfahrt());
    }
    @Test
    public void abholenPartner() {
        SchiriConfiguration config = SchiriConfiguration.NEW_DEFAULT("");
        config.Spielablauf.UmziehenVorSpiel = 15;
        String liga = "Landesliga Männer";
        LocalDateTime anwurf = LocalDateTime.parse("2024-04-13T15:30:00");
        Fahrt zurHalle = new Fahrt(45, 0);
        Fahrt zumPartner = new Fahrt(15, 0);
        SchirieinsatzAblauf ablauf = new SchirieinsatzAblauf(anwurf, liga, zurHalle, zumPartner, config, technBespr30Min());

        assertEquals(LocalDateTime.parse("2024-04-13T14:00:00"), ablauf.getPartnerAbholen());
    }
    @Test
    public void abfahrtBeiZweiSchiris() {
        SchiriConfiguration config = SchiriConfiguration.NEW_DEFAULT("");
        config.Spielablauf.UmziehenVorSpiel = 15;
        String liga = "Landesliga Männer";
        LocalDateTime anwurf = LocalDateTime.parse("2024-04-13T15:30:00");
        Fahrt zurHalle = new Fahrt(45, 0);
        Fahrt zumPartner = new Fahrt(15, 0);
        SchirieinsatzAblauf ablauf = new SchirieinsatzAblauf(anwurf, liga, zurHalle, zumPartner, config, technBespr30Min());

        assertEquals(LocalDateTime.parse("2024-04-13T13:45:00"), ablauf.getAbfahrt());
    }

    @Test
    public void spielEnde() {
        SchiriConfiguration config = SchiriConfiguration.NEW_DEFAULT("");
        config.Spielablauf.EffektiveSpielDauer = 90;
        LocalDateTime anwurf = LocalDateTime.parse("2024-04-13T15:30:00");
        SchirieinsatzAblauf ablauf = new SchirieinsatzAblauf(anwurf, "", null,  config, null);

        assertEquals(LocalDateTime.parse("2024-04-13T17:00:00"), ablauf.getSpielEnde());
    }

    @Test
    public void rueckfahrt() {
        SchiriConfiguration config = SchiriConfiguration.NEW_DEFAULT("");
        config.Spielablauf.EffektiveSpielDauer = 90;
        config.Spielablauf.PapierKramNachSpiel = 15;
        config.Spielablauf.UmziehenNachSpiel = 15;
        LocalDateTime anwurf = LocalDateTime.parse("2024-04-13T15:30:00");
        SchirieinsatzAblauf ablauf = new SchirieinsatzAblauf(anwurf, "", null, config, null);

        assertEquals(LocalDateTime.parse("2024-04-13T17:30:00"), ablauf.getRueckfahrt());
    }

    @Test
    public void heimkehrEinSchiri() {
        SchiriConfiguration config = SchiriConfiguration.NEW_DEFAULT("");
        config.Spielablauf.EffektiveSpielDauer = 90;
        config.Spielablauf.PapierKramNachSpiel = 15;
        config.Spielablauf.UmziehenNachSpiel = 15;
        LocalDateTime anwurf = LocalDateTime.parse("2024-04-13T15:30:00");
        Fahrt zurHalle = new Fahrt(45, 0);
        SchirieinsatzAblauf ablauf = new SchirieinsatzAblauf(anwurf, "", zurHalle, config, null);

        assertEquals(LocalDateTime.parse("2024-04-13T18:15:00"), ablauf.getHeimkehr());
    }
    @Test
    public void zurückbringenPartner() {
        SchiriConfiguration config = SchiriConfiguration.NEW_DEFAULT("");
        config.Spielablauf.EffektiveSpielDauer = 90;
        config.Spielablauf.PapierKramNachSpiel = 15;
        config.Spielablauf.UmziehenNachSpiel = 15;
        LocalDateTime anwurf = LocalDateTime.parse("2024-04-13T15:30:00");
        Fahrt zurHalle = new Fahrt(45, 0);
        Fahrt zumPartner = new Fahrt(15, 0);
        SchirieinsatzAblauf ablauf = new SchirieinsatzAblauf(anwurf, "", zurHalle, zumPartner, config, null);

        assertEquals(LocalDateTime.parse("2024-04-13T18:15:00"), ablauf.getZurueckbringenPartner());
    }
    @Test
    public void heimkehrZweiSchiris() {
        SchiriConfiguration config = SchiriConfiguration.NEW_DEFAULT("");
        config.Spielablauf.EffektiveSpielDauer = 90;
        config.Spielablauf.PapierKramNachSpiel = 15;
        config.Spielablauf.UmziehenNachSpiel = 15;
        LocalDateTime anwurf = LocalDateTime.parse("2024-04-13T15:30:00");
        Fahrt zurHalle = new Fahrt(45, 0);
        Fahrt zumPartner = new Fahrt(15, 0);
        SchirieinsatzAblauf ablauf = new SchirieinsatzAblauf(anwurf, "", zurHalle, zumPartner, config, null);

        assertEquals(LocalDateTime.parse("2024-04-13T18:30:00"), ablauf.getHeimkehr());
    }

}