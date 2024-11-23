package com.dieblich.handball.schiedsrichterassistent.mail.received;

import com.dieblich.handball.schiedsrichterassistent.Schiedsrichter;
import com.dieblich.handball.schiedsrichterassistent.SchiriEinsatz;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AnsetzungsEmailTest {

    @Test
    public void checkFullSample(){
        String emailContent = """
            Sehr geehrte Damen und Herren,
            
            für die folgenden Begegnungen sind Schiedsrichter bzw. Beobachter eingeteilt worden:
            
            Olga Kebap: o.kebap@muster.de
            Änderung: Gespann // o.kebap@muster.de
            Liga: Kreisliga männliche Jugend A
            Staffel:
            Kreisliga männliche Jugend A
            Spiel-Nr: 10078
            25.02.2024 16:30 KiTA Handball Köln III - TSV Ballspiel II
            Ort: 06041 Köln Schule, Schulstraße 3, 54321 Köln
            SR-Gespann: Maxipfeife Moritz
            SR-Gespann alt: Mustermann Max
            """;
        SchiriEinsatz actual = AnsetzungsEmail.extractSchiriEinsaetze(emailContent).get(0);
        SchiriEinsatz expected = new SchiriEinsatz(
                "10078",
                LocalDateTime.of(2024, 2, 25, 16, 30),
                "06041 Köln Schule",
                "Schulstraße 3",
                "54321 Köln",
                "Kreisliga männliche Jugend A",
                "KiTA Handball Köln III",
                "TSV Ballspiel II",
                new Schiedsrichter("Moritz", "Maxipfeife"),
                null
        );
        assertEquals(expected, actual);
    }

    @Test
    public void checkNameDetection() {
        String emailContent = """
                Gesendet von Mail für Windows
                
                Von: nuLiga Handball
                Gesendet: Dienstag, 20. Februar 2024 10:40
                An: max@muster.de
                Betreff: Info-Mail: Spielansetzung Schiedsrichter bzw. Beobachter
                
                Sehr geehrte Damen und Herren,
                
                für die folgenden Begegnungen sind Schiedsrichter bzw. Beobachter eingeteilt worden:
                
                Gerd Hummel: g.hummel@dhb.de
                Änderung: Gespann // g.hummel@dhb.de
                Liga: Kreisliga Männer
                Staffel:\s
                Kreisliga Männer
                Spiel-Nr: 1137
                02.03.2024 18:00 Brühler TV - TV Jahn Köln-Wahn II
                Ort: 06076 BTV-Sportzentrum, von-Wied-Straße 2, 50321 Brühl
                SR-Gespann: Mustermann Max
                SR-Gespann alt: Klowni Carsten / Lümmel Dominik
                """;

        SchiriEinsatz actual = AnsetzungsEmail.extractSchiriEinsaetze(emailContent).get(0);
        SchiriEinsatz expected = new SchiriEinsatz(
                "1137",
                LocalDateTime.of(2024, 3, 2, 18, 0),
                "06076 BTV-Sportzentrum",
                "von-Wied-Straße 2",
                "50321 Brühl",
                "Kreisliga Männer",
                "Brühler TV",
                "TV Jahn Köln-Wahn II",
                new Schiedsrichter("Max", "Mustermann"),
                null
        );
        assertEquals(expected, actual);
    }
    @Test
    public void checkTwoSchiedsrichter(){
        String emailContent = """
                Sehr geehrte Damen und Herren,

                für die folgenden Begegnungen sind Schiedsrichter bzw. Beobachter eingeteilt worden:

                Robert Möllemann: robert.moellemann@handball-qatar.de
                Änderung: Gespann // robert.moellemann@handball-qatar.de
                Liga: Mittelrhein Oberliga Frauen
                Staffel:\s
                Oberliga Frauen Gr. 2 (HVM)
                Spiel-Nr: 21131
                16.03.2024 16:00 SSV Nümbrecht Handball - HV Erftstadt
                Ort: 09004 GWN-Arena, Mateh-Yehuda-Str. 3a, 51588 Nümbrecht
                SR-Gespann: Witz Martin / Kohlenfluss Andre
                SR-Gespann alt: -
                """;

        SchiriEinsatz actual = AnsetzungsEmail.extractSchiriEinsaetze(emailContent).get(0);
        SchiriEinsatz expected = new SchiriEinsatz(
                "21131",
                LocalDateTime.of(2024, 3, 16, 16, 0),
                "09004 GWN-Arena",
                "Mateh-Yehuda-Str. 3a",
                "51588 Nümbrecht",
                "Mittelrhein Oberliga Frauen",
                "SSV Nümbrecht Handball",
                "HV Erftstadt",
                new Schiedsrichter("Martin", "Witz"),
                new Schiedsrichter("Andre", "Kohlenfluss")
        );
        assertEquals(expected, actual);
    }


    @Test
    public void oneEmailTwoAnsetzungen(){
        String emailContent = """
            Sehr geehrte Damen und Herren,
            
            für die folgenden Begegnungen sind Schiedsrichter bzw. Beobachter eingeteilt worden:
            
            Olga Kebap: o.kebap@muster.de
            Änderung: Gespann // o.kebap@muster.de
            Liga: Kreisliga männliche Jugend A
            Staffel:
            Kreisliga männliche Jugend A
            Spiel-Nr: 10078
            25.02.2024 16:30 KiTA Handball Köln III - TSV Ballspiel II
            Ort: 06041 Köln Schule, Schulstraße 3, 54321 Köln
            SR-Gespann: Maxipfeife Moritz / Wippe Jürgen
            SR-Gespann alt: Mustermann Max
            
            Änderung: Gespann // o.kebap@muster.de
            Liga: Oberliga Frauen
            Staffel:
            Oberliga Frauen Gr. 3
            Spiel-Nr: 4711
            26.03.2025 17:24 Trinkzessinnen IV - TSV Saufen Feiern I
            Ort: 01901 Berlin Halle, Hauptstraße 17, 12345 Berlin
            SR-Gespann: Maxipfeife Moritz
            SR-Gespann alt: Mustermann Max
            """;
        List<SchiriEinsatz> actual = AnsetzungsEmail.extractSchiriEinsaetze(emailContent);
        SchiriEinsatz expectedA = new SchiriEinsatz(
                "10078",
                LocalDateTime.of(2024, 2, 25, 16, 30),
                "06041 Köln Schule",
                "Schulstraße 3",
                "54321 Köln",
                "Kreisliga männliche Jugend A",
                "KiTA Handball Köln III",
                "TSV Ballspiel II",
                new Schiedsrichter("Moritz", "Maxipfeife"),
                new Schiedsrichter("Jürgen", "Wippe")
        );
        SchiriEinsatz expectedB = new SchiriEinsatz(
                "4711",
                LocalDateTime.of(2025, 3, 26, 17, 24),
                "01901 Berlin Halle",
                "Hauptstraße 17",
                "12345 Berlin",
                "Oberliga Frauen",
                "Trinkzessinnen IV",
                "TSV Saufen Feiern I",
                new Schiedsrichter("Moritz", "Maxipfeife"),
                null
        );
        assertEquals(List.of(expectedA, expectedB), actual);
    }


    @Test
    public void checkForwardedWithIntendedAnsetzung(){
        String emailContent = """
            Von meinem Nokia6210
            
            Anfang der weitergeleiteten Nachricht:
            
            > Von: nuLiga Handball <no-reply@liga.nu>
            > Datum: 23. November 2024 um 10:38:34 MEZ
            > An: martin@witze.de
            > Betreff: Info-Mail: Spielansetzung Schiedsrichter bzw. Beobachter
            > Antwort an: "karies@icloud.com" <karies@icloud.com>
            >\s
            > ﻿
            > Sehr geehrte Damen und Herren,
            >\s
            > für die folgenden Begegnungen sind Schiedsrichter bzw. Beobachter eingeteilt worden:
            >\s
            > Achim Zweiter: a@zweiter.de
            > Änderung: Gespann // a@zweiter.de
            > Liga: Verbandsliga Männer
            > Staffel:
            > Verbandsliga Männer Gr. 5
            > Spiel-Nr: 123456
            > 23.11.2024 17:00 Pfadfinder Oeventrop II - Handball Opfer
            > Ort: 03044 Sporthalle Pfadfinder, Höhenring 101, 44358 Oeventrop
            > SR-Gespann: Witz Martin
            > SR-Gespann alt: Witz Martin / Kohlenfuss Andre
            >\s
            >\s
            """;
        List<SchiriEinsatz> einsaetze = AnsetzungsEmail.extractSchiriEinsaetze(emailContent);
        assertFalse(einsaetze.isEmpty(), "Nothing found");
        SchiriEinsatz actual = einsaetze.get(0);
        SchiriEinsatz expected = new SchiriEinsatz(
                "123456",
                LocalDateTime.of(2024, 11, 23, 17, 0),
                "03044 Sporthalle Pfadfinder",
                "Höhenring 101",
                "44358 Oeventrop",
                "Verbandsliga Männer",
                "Pfadfinder Oeventrop II",
                "Handball Opfer",
                new Schiedsrichter("Martin", "Witz"),
                null
        );
        assertEquals(expected, actual);
    }

    @Test
    public void checkForwardedWithMultipleTimesIntendedAnsetzung(){
        String emailContent = """
            Von meinem Nokia6210
            
            Anfang der weitergeleiteten Nachricht:
            
            > Von: Martin Witz <martin@witze.de>
            > Datum: 23. November 2024 um 11:26:09 MEZ
            > An: schiribot@witze.de
            > Betreff: Wtr: Info-Mail: Spielansetzung Schiedsrichter bzw. Beobachter
            >\s
            > ﻿
            > Von meinem Nokia6210
            >\s
            > Anfang der weitergeleiteten Nachricht:
            >\s
            >> Von: nuLiga Handball <no-reply@liga.nu>
            >> Datum: 23. November 2024 um 10:38:34 MEZ
            >> An: martin@witze.de
            >> Betreff: Info-Mail: Spielansetzung Schiedsrichter bzw. Beobachter
            >> Antwort an: "karies@icloud.com" <karies@icloud.com>
            >>\s
            >> ﻿
            >> Sehr geehrte Damen und Herren,
            >>\s
            >> für die folgenden Begegnungen sind Schiedsrichter bzw. Beobachter eingeteilt worden:
            >>\s
            >> Achim Zweiter: a@zweiter.de
            >> Änderung: Gespann // a@zweiter.de
            >> Liga: Verbandsliga Männer
            >> Staffel:
            >> Verbandsliga Männer Gr. 5
            >> Spiel-Nr: 123456
            >> 23.11.2024 17:00 Pfadfinder Oeventrop II - Handball Opfer
            >> Ort: 03044 Sporthalle Pfadfinder, Höhenring 101, 44358 Oeventrop
            >> SR-Gespann: Witz Martin
            >> SR-Gespann alt: Witz Martin / Kohlenfuss Andre
            >>\s
            >>\s
            """;
        List<SchiriEinsatz> einsaetze = AnsetzungsEmail.extractSchiriEinsaetze(emailContent);
        assertFalse(einsaetze.isEmpty(), "Nothing found");
        SchiriEinsatz actual = einsaetze.get(0);
        SchiriEinsatz expected = new SchiriEinsatz(
                "123456",
                LocalDateTime.of(2024, 11, 23, 17, 0),
                "03044 Sporthalle Pfadfinder",
                "Höhenring 101",
                "44358 Oeventrop",
                "Verbandsliga Männer",
                "Pfadfinder Oeventrop II",
                "Handball Opfer",
                new Schiedsrichter("Martin", "Witz"),
                null
        );
        assertEquals(expected, actual);
    }

}