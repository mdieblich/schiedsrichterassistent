package com.dieblich.handball.schiedsrichterassistent.mail.received;

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
            SR-Gespann: Moritz Maxipfeife
            SR-Gespann alt: Max Mustermann
            """;
        SchiriEinsatz actual = AnsetzungsEmail.extractSchiriEinsatz(emailContent);
        SchiriEinsatz expected = new SchiriEinsatz(
                LocalDateTime.of(2024, 2, 25, 16, 30),
                "Schulstraße 3, 54321 Köln",
                "Kreisliga männliche Jugend A",
                "KiTA Handball Köln III",
                "TSV Ballspiel II"
        );
        assertEquals(expected, actual);
    }

    @Test
    public void checkNameDetection(){
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
                "von-Wied-Straße 2, 50321 Brühl",
                "Kreisliga Männer",
                "Brühler TV",
                "TV Jahn Köln-Wahn II"
        );
        assertEquals(expected, actual);
    }

}