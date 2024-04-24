package com.dieblich.handball.schiedsrichterassistent.mail.received;

import com.dieblich.handball.schiedsrichterassistent.Schiedsrichter;
import com.dieblich.handball.schiedsrichterassistent.SchiriEinsatz;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

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
        SchiriEinsatz actual = AnsetzungsEmail.extractSchiriEinsatz(emailContent);
        SchiriEinsatz expected = new SchiriEinsatz(
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

        SchiriEinsatz actual = AnsetzungsEmail.extractSchiriEinsatz(emailContent);
        SchiriEinsatz expected = new SchiriEinsatz(
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

        SchiriEinsatz actual = AnsetzungsEmail.extractSchiriEinsatz(emailContent);
        SchiriEinsatz expected = new SchiriEinsatz(
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

}