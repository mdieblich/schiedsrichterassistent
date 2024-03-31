package com.dieblich.handball.schiedsrichterassistent;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class SchiedsrichterTest {

    @ParameterizedTest
    @CsvSource({
            "Witz Martin, Martin, Witz",
            "Witz Martin Wolfgang, Martin Wolfgang, Witz",
    })
    public void createFromNachnameVorname(String input, String vornamen, String nachnamen){
        Schiedsrichter created = Schiedsrichter.fromNachnameVorname(input);
        Schiedsrichter expected = new Schiedsrichter(vornamen, nachnamen);
        assertEquals(expected, created);
    }

}