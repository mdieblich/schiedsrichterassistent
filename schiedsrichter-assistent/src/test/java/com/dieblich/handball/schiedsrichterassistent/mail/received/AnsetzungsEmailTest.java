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

}