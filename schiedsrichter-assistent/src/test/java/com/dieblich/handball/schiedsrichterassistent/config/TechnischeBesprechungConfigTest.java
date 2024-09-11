package com.dieblich.handball.schiedsrichterassistent.config;

import com.dieblich.handball.schiedsrichterassistent.calendar.SchirieinsatzAblauf;
import com.dieblich.handball.schiedsrichterassistent.geo.Fahrt;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TechnischeBesprechungConfigTest {

    @Test
    public void createsFileIfNotExists() {
        // arrange
        File configFile = new File(TechnischeBesprechungConfigurationFile.FILE_NAME);
        if(configFile.exists()){
            boolean successfullyDeleted = configFile.delete();
            assertTrue(successfullyDeleted, "Precondition failed: File not deleted!");
        }

        // act
        try {
            TechnischeBesprechungConfiguration.loadOrCreate();
        } catch (Exception e){ /* ignore all exceptions */ }

        // assert
        assertTrue(configFile.exists());
    }

    private SchirieinsatzAblauf createAblauf(String liga){
        return new SchirieinsatzAblauf(
                null,
                liga,
                null,
                null
        );
    }

    @Test
    public void readsContentFromConfigFile() throws ConfigException {
        // arrange
        TechnischeBesprechungConfiguration modifiedConfig = TechnischeBesprechungConfiguration.defaultConfig();
        modifiedConfig.add("Opferliga Männer", 50);
        TechnischeBesprechungConfigurationFile.defaultConfigFile().save(modifiedConfig);
        SchirieinsatzAblauf ablauf = createAblauf("Opferliga Männer");

        // act
        TechnischeBesprechungConfiguration config = TechnischeBesprechungConfiguration.loadOrCreate();
        int dauer = config.get(ablauf);

        // assert
        assertEquals(50, dauer );
    }
    @Test
    public void readsNewContentFromConfigFile() throws ConfigException {
        // arrange
        TechnischeBesprechungConfiguration modifiedConfig = TechnischeBesprechungConfiguration.defaultConfig();
        modifiedConfig.standard = 40;
        TechnischeBesprechungConfigurationFile.defaultConfigFile().save(modifiedConfig);
        SchirieinsatzAblauf ablauf = createAblauf("Opferliga Männer");

        // act
        TechnischeBesprechungConfiguration config = TechnischeBesprechungConfiguration.loadOrCreate();
        int dauer = config.get(ablauf);

        // assert
        assertEquals(40, dauer);
    }

    @ParameterizedTest
    @CsvSource({

            // HVN, Senioren
            "Regionalliga Männer,                45",
            "Regionalliga Frauen,                45",

            "Oberliga Männer Gr. 1,              45",
            "Oberliga Männer Gr. 2,              45",
            "Oberliga Frauen Gr. 1,              45",
            "Oberliga Frauen Gr. 2,              45",

            "Verbandsliga Männer Gr. 1,          30",
            "Verbandsliga Männer Gr. 4,          30",
            "Verbandsliga Frauen Gr. 2,          30",
            "Verbandsliga Frauen Gr. 3,          30",

            // HVN, Jugend
            "Regionalliga männliche Jugend A,    30",
            "Regionalliga weibliche Jugend A,    30",
            "Regionalliga männliche Jugend B,    30",
            "Regionalliga weibliche Jugend B,    30",
            "Regionalliga männliche Jugend C,    30",
            "Regionalliga weibliche Jugend C,    30",

            "Oberliga männliche Jugend A,        30",
            "Oberliga weibliche Jugend A,        30",
            "Oberliga männliche Jugend B,        30",
            "Oberliga weibliche Jugend B,        30",
            "Oberliga männliche Jugend C,        30",
            "Oberliga weibliche Jugend C,        30",

            // Kreis, Senioren
            "Regionsoberliga Männer,             30",
            "Regionsoberliga Frauen,             30",

            "Regionsliga Männer,                 30",
            "Regionsklasse Männer,               30",
            "2. Regionsklasse Männer,            30",
            "Regionsliga Frauen,                 30",

            // Kreis, Jugend
            "Regionsoberliga männliche Jugend A, 30",
            "Regionsoberliga weibliche Jugend A, 30",
            "Regionsoberliga männliche Jugend B, 30",
            "Regionsoberliga weibliche Jugend B, 30",
            "Regionsoberliga männliche Jugend C, 30",
            "Regionsoberliga weibliche Jugend C, 30",
            "Regionsoberliga männliche Jugend D, 30",
            "Regionsoberliga weibliche Jugend D, 30",
            "Regionsoberliga gemischte Jugend D, 30",
            "Regionsoberliga männliche Jugend E, 30",
            "Regionsoberliga weibliche Jugend E, 30",
            "Regionsoberliga gemischte Jugend E, 30",

    })
    public void dauerCheckForAllLigen(String liga, int expectedDauer) throws ConfigException {
        SchirieinsatzAblauf ablauf = createAblauf(liga);

        // act
        TechnischeBesprechungConfiguration config = TechnischeBesprechungConfiguration.loadOrCreate();
        int dauer = config.get(ablauf);

        assertEquals(expectedDauer, dauer);
    }

    @Test
    public void berechneAusAblauf() throws ConfigException {
        // arrange
        SchirieinsatzAblauf ablauf = new SchirieinsatzAblauf(
                null,
                "Oberliga Frauen Gr. 2",
                null,
                null
        );

        // act
        TechnischeBesprechungConfiguration config = TechnischeBesprechungConfiguration.loadOrCreate();
        int dauer = config.get(ablauf);

        // assert
        assertEquals(45, dauer);
    }
}