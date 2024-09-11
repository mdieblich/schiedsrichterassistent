package com.dieblich.handball.schiedsrichterassistent.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TechnischeBesprechungConfiguration {
    public Integer standard;
    public Map<String, Integer> abweichungen = new HashMap<>();

    public static TechnischeBesprechungConfiguration loadOrCreate() throws ConfigException {
        TechnischeBesprechungConfigurationFile configFile = TechnischeBesprechungConfigurationFile.defaultConfigFile();
        if(!configFile.exists()){
            configFile.save(TechnischeBesprechungConfiguration.defaultConfig());
        }
        return configFile.load();
    }

    public int get(String ligaBezeichnungAusEmail) {

        String trimmedLiga = ligaBezeichnungAusEmail
                .toLowerCase()
                .replaceAll("gr. \\d", "")
                .trim();
        Integer dauerForLiga = abweichungen.get(trimmedLiga);
        if(dauerForLiga == null){
            dauerForLiga = standard;
        }
        return dauerForLiga;
    }

    private TechnischeBesprechungConfiguration(){}

    public void add(String liga, int dauer){
        abweichungen.put(liga.trim().toLowerCase(), dauer);
    }

    public TechnischeBesprechungConfiguration(Map<String, Integer> configRows){
        standard = configRows.remove("Standard");
        abweichungen = configRows;
    }

    public static final List<String> MANDATORY_KEYS = List.of(
            "Standard"
    );

    public static TechnischeBesprechungConfiguration defaultConfig() {
        TechnischeBesprechungConfiguration defaultConfiguration = new TechnischeBesprechungConfiguration();

        defaultConfiguration.standard = 30;

        defaultConfiguration.add("Regionalliga Männer                ",45);
        defaultConfiguration.add("Oberliga Männer                    ",45);
        defaultConfiguration.add("Regionalliga Frauen                ",45);
        defaultConfiguration.add("Oberliga Frauen                    ",45);

        return defaultConfiguration;
    }
}
