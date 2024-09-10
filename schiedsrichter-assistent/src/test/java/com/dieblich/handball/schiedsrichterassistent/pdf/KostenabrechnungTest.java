package com.dieblich.handball.schiedsrichterassistent.pdf;

import com.dieblich.handball.schiedsrichterassistent.config.*;
import com.dieblich.handball.schiedsrichterassistent.SchiriEinsatz;
import com.dieblich.handball.schiedsrichterassistent.calendar.SchirieinsatzAblauf;
import com.dieblich.handball.schiedsrichterassistent.geo.Fahrt;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class KostenabrechnungTest {

    @Test
    public void calculatesKosten() throws ConfigException {
        SchiriConfiguration configA = SchiriConfiguration.NEW_DEFAULT("emailA");
        configA.Benutzerdaten.Vorname = "Max";
        configA.Benutzerdaten.Nachname = "Mustermann";
        configA.Benutzerdaten.Adresse = "Musterstr. 17, 12345 Köln";

        SchiriConfiguration configB = SchiriConfiguration.NEW_DEFAULT("emailB");
        configB.Benutzerdaten.Vorname = "Thea";
        configB.Benutzerdaten.Nachname = "Testnutzer";
        configB.Benutzerdaten.Adresse = "Testallee. 12, 54321 Dortmund";

        SchiriEinsatz einsatz = new SchiriEinsatz("13156",
                LocalDateTime.of(2024,4,13,15,30,0),
                "AC2 Aachen Gillesbachtal",
                "Branderhofer Weg 15",
                "52066 Aachen",
                "Regionsoberliga Frauen",
                "BTB Aachen III",
                "SG Olheim-Straßfeld",
                configA.Benutzerdaten.getSchiedsrichter(),
                configB.Benutzerdaten.getSchiedsrichter()
        );
        SchirieinsatzAblauf ablauf = new SchirieinsatzAblauf(
                einsatz.anwurf(),
                einsatz.ligaBezeichnungAusEmail(),
                new Fahrt(30*60, 35),
                new Fahrt(10*60, 5),
                configA
        );
        Kostenabrechnung abr = new Kostenabrechnung(einsatz, ablauf, KostenConfiguration.defaultConfig(), configA, configB);
        LigaKosten ligaKosten = new LigaKosten(25.0, 0.35, 0.05);
        assertEquals(new Schirikosten(ligaKosten, 80, 70), abr.getSchirikosten());
    }

    @Test
    public void createTestFile() throws IOException, ConfigException {
        SchiriConfiguration configA = SchiriConfiguration.NEW_DEFAULT("emailA");
        configA.Benutzerdaten.Vorname = "Max";
        configA.Benutzerdaten.Nachname = "Mustermann";
        configA.Benutzerdaten.Adresse = "Musterstr. 17, 12345 Köln";

        SchiriConfiguration configB = SchiriConfiguration.NEW_DEFAULT("emailB");
        configB.Benutzerdaten.Vorname = "Thea";
        configB.Benutzerdaten.Nachname = "Testnutzer";
        configB.Benutzerdaten.Adresse = "Testallee. 12, 54321 Dortmund";

        SchiriEinsatz einsatz = new SchiriEinsatz("13156",
                LocalDateTime.of(2024,4,13,15,30,0),
                "AC2 Aachen Gillesbachtal",
                "Branderhofer Weg 15",
                "52066 Aachen",
                "Regionsoberliga Frauen",
                "BTB Aachen III",
                "SG Olheim-Straßfeld",
                configA.Benutzerdaten.getSchiedsrichter(),
                configB.Benutzerdaten.getSchiedsrichter()
        );
        SchirieinsatzAblauf ablauf = new SchirieinsatzAblauf(
                einsatz.anwurf(),
                einsatz.ligaBezeichnungAusEmail(),
                new Fahrt(30*60, 35),
                new Fahrt(10*60, 5),
                configA
        );
        Kostenabrechnung abr = new Kostenabrechnung(einsatz, ablauf, KostenConfiguration.defaultConfig(), configA, configB);
        abr.exportToPDF("kostenabrechnung.pdf");
    }
}