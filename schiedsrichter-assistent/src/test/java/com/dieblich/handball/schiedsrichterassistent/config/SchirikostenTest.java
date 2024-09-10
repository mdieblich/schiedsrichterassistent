package com.dieblich.handball.schiedsrichterassistent.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class SchirikostenTest {

    @Test
    public void createsFileIfNotExists() {
        // arrange
        File configFile = new File(KostenConfigurationFile.FILE_NAME);
        if(configFile.exists()){
            boolean successfullyDeleted = configFile.delete();
            assertTrue(successfullyDeleted, "Precondition failed: File not deleted!");
        }

        // act
        try {
            KostenConfiguration.calculate("Regionalliga Frauen", 20);
        } catch (Exception e){ /* ignore all exceptions */ }

        // assert
        assertTrue(configFile.exists());
    }

    @Test
    public void readsContentFromConfigFile() throws IOException, ConfigException {
        // arrange
        KostenConfiguration modifiedConfig = KostenConfiguration.defaultConfig();
        modifiedConfig.add("Opferliga Männer", 12.5, 0.10, 0.00);
        KostenConfigurationFile.defaultConfigFile().save(modifiedConfig);

        // act
        Schirikosten kosten = KostenConfiguration.calculate("Opferliga Männer", 20);

        // assert
        assertEquals(12.5, kosten.teilnahmeEntschädigung() );
        assertEquals(2, kosten.fahrtKostenFahrer() );
    }

    @Test
    public void sanitizesTheConfig() {
        fail("TODO: Implement");
    }

    @ParameterizedTest
    @CsvSource({

            // HVN, Senioren
            "Regionalliga Männer,                80.00",
            "Regionalliga Frauen,                60.00",

            "Oberliga Männer Gr. 1,              50.00",
            "Oberliga Männer Gr. 2,              50.00",
            "Oberliga Frauen Gr. 1,              40.00",
            "Oberliga Frauen Gr. 2,              40.00",

            "Verbandsliga Männer Gr. 1,          40.00",
            "Verbandsliga Männer Gr. 4,          40.00",
            "Verbandsliga Frauen Gr. 2,          30.00",
            "Verbandsliga Frauen Gr. 3,          30.00",

            // HVN, Jugend
            "Regionalliga männliche Jugend A,    35.00",
            "Regionalliga weibliche Jugend A,    35.00",
            "Regionalliga männliche Jugend B,    30.00",
            "Regionalliga weibliche Jugend B,    30.00",
            "Regionalliga männliche Jugend C,    30.00",
            "Regionalliga weibliche Jugend C,    30.00",

            "Oberliga männliche Jugend A,        30.00",
            "Oberliga weibliche Jugend A,        30.00",
            "Oberliga männliche Jugend B,        30.00",
            "Oberliga weibliche Jugend B,        30.00",
            "Oberliga männliche Jugend C,        30.00",
            "Oberliga weibliche Jugend C,        30.00",

            // Kreis, Senioren
            "Regionsoberliga Männer,             25.00",
            "Regionsoberliga Frauen,             25.00",

            "Regionsliga Männer,                 22.50",
            "Regionsklasse Männer,               22.50",
            "2. Regionsklasse Männer,            22.50",
            "Regionsliga Frauen,                 22.50",

            // Kreis, Jugend
            "Regionsoberliga männliche Jugend A, 22.50",
            "Regionsoberliga weibliche Jugend A, 22.50",
            "Regionsoberliga männliche Jugend B, 20.00",
            "Regionsoberliga weibliche Jugend B, 20.00",
            "Regionsoberliga männliche Jugend C, 20.00",
            "Regionsoberliga weibliche Jugend C, 20.00",
            "Regionsoberliga männliche Jugend D, 16.00",
            "Regionsoberliga weibliche Jugend D, 16.00",
            "Regionsoberliga gemischte Jugend D, 16.00",
            "Regionsoberliga männliche Jugend E, 16.00",
            "Regionsoberliga weibliche Jugend E, 16.00",
            "Regionsoberliga gemischte Jugend E, 16.00",

    })
    public void teilnahmeEntschädigung(String liga, double expectedEntschädigung) throws ConfigException {
        Schirikosten kosten = KostenConfiguration.calculate(liga, 30);

        assertEquals(expectedEntschädigung, kosten.teilnahmeEntschädigung());
    }

    @ParameterizedTest
    @CsvSource({

            // HVN, Senioren
            "Regionalliga Männer,                 9.00",
            "Regionalliga Frauen,                 9.00",

            "Oberliga Männer Gr. 1,               9.00",
            "Oberliga Männer Gr. 2,               9.00",
            "Oberliga Frauen Gr. 1,               9.00",
            "Oberliga Frauen Gr. 2,               9.00",

            "Verbandsliga Männer Gr. 1,           9.00",
            "Verbandsliga Männer Gr. 4,           9.00",
            "Verbandsliga Frauen Gr. 2,           9.00",
            "Verbandsliga Frauen Gr. 3,           9.00",

            // HVN, Jugend
            "Regionalliga männliche Jugend A,     9.00",
            "Regionalliga weibliche Jugend A,     9.00",
            "Regionalliga männliche Jugend B,     9.00",
            "Regionalliga weibliche Jugend B,     9.00",
            "Regionalliga männliche Jugend C,     9.00",
            "Regionalliga weibliche Jugend C,     9.00",

            "Oberliga männliche Jugend A,         9.00",
            "Oberliga weibliche Jugend A,         9.00",
            "Oberliga männliche Jugend B,         9.00",
            "Oberliga weibliche Jugend B,         9.00",
            "Oberliga männliche Jugend C,         9.00",
            "Oberliga weibliche Jugend C,         9.00",

            // Kreis, Senioren
            "Regionsoberliga Männer,             10.50",
            "Regionsoberliga Frauen,             10.50",

            "Regionsliga Männer,                 10.50",
            "Regionsklasse Männer,               10.50",
            "2. Regionsklasse Männer,            10.50",
            "Regionsliga Frauen,                 10.50",

            // Kreis, Jugend
            "Regionsoberliga männliche Jugend A, 10.50",
            "Regionsoberliga weibliche Jugend A, 10.50",
            "Regionsoberliga männliche Jugend B, 10.50",
            "Regionsoberliga weibliche Jugend B, 10.50",
            "Regionsoberliga männliche Jugend C, 10.50",
            "Regionsoberliga weibliche Jugend C, 10.50",
            "Regionsoberliga männliche Jugend D, 10.50",
            "Regionsoberliga weibliche Jugend D, 10.50",
            "Regionsoberliga gemischte Jugend D, 10.50",
            "Regionsoberliga männliche Jugend E, 10.50",
            "Regionsoberliga weibliche Jugend E, 10.50",
            "Regionsoberliga gemischte Jugend E, 10.50",

    })
    public void fahrtkosten30kmFahrer(String liga, double expectedFahrtkosten) throws ConfigException {
        Schirikosten kosten = KostenConfiguration.calculate(liga, 30);

        assertEquals(expectedFahrtkosten, kosten.fahrtKostenFahrer());
    }

    @ParameterizedTest
    @CsvSource({

            // HVN, Senioren
            "Regionalliga Männer,                 0.00",
            "Regionalliga Frauen,                 0.00",

            "Oberliga Männer Gr. 1,               0.00",
            "Oberliga Männer Gr. 2,               0.00",
            "Oberliga Frauen Gr. 1,               0.00",
            "Oberliga Frauen Gr. 2,               0.00",

            "Verbandsliga Männer Gr. 1,           0.00",
            "Verbandsliga Männer Gr. 4,           0.00",
            "Verbandsliga Frauen Gr. 2,           0.00",
            "Verbandsliga Frauen Gr. 3,           0.00",

            // HVN, Jugend
            "Regionalliga männliche Jugend A,     0.00",
            "Regionalliga weibliche Jugend A,     0.00",
            "Regionalliga männliche Jugend B,     0.00",
            "Regionalliga weibliche Jugend B,     0.00",
            "Regionalliga männliche Jugend C,     0.00",
            "Regionalliga weibliche Jugend C,     0.00",

            "Oberliga männliche Jugend A,         0.00",
            "Oberliga weibliche Jugend A,         0.00",
            "Oberliga männliche Jugend B,         0.00",
            "Oberliga weibliche Jugend B,         0.00",
            "Oberliga männliche Jugend C,         0.00",
            "Oberliga weibliche Jugend C,         0.00",

            // Kreis, Senioren
            "Regionsoberliga Männer,              1.50",
            "Regionsoberliga Frauen,              1.50",

            "Regionsliga Männer,                  1.50",
            "Regionsklasse Männer,                1.50",
            "2. Regionsklasse Männer,             1.50",
            "Regionsliga Frauen,                  1.50",

            // Kreis, Jugend
            "Regionsoberliga männliche Jugend A,  1.50",
            "Regionsoberliga weibliche Jugend A,  1.50",
            "Regionsoberliga männliche Jugend B,  1.50",
            "Regionsoberliga weibliche Jugend B,  1.50",
            "Regionsoberliga männliche Jugend C,  1.50",
            "Regionsoberliga weibliche Jugend C,  1.50",
            "Regionsoberliga männliche Jugend D,  1.50",
            "Regionsoberliga weibliche Jugend D,  1.50",
            "Regionsoberliga gemischte Jugend D,  1.50",
            "Regionsoberliga männliche Jugend E,  1.50",
            "Regionsoberliga weibliche Jugend E,  1.50",
            "Regionsoberliga gemischte Jugend E,  1.50",

    })
    public void fahrtkosten30kmBeifahrer(String liga, double expectedFahrtkosten) throws ConfigException {
        Schirikosten kosten = KostenConfiguration.calculate(liga, 30);

        assertEquals(expectedFahrtkosten, kosten.fahrtKostenBeifahrer());
    }
}