package com.dieblich.handball.schiedsrichterassistent.config;

import com.dieblich.handball.schiedsrichterassistent.calendar.SchirieinsatzAblauf;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ToString
@EqualsAndHashCode
public class KostenConfiguration {

    public LigaKosten StandardSenioren;
    public LigaKosten StandardJugendA;
    public LigaKosten StandardJugendB;
    public LigaKosten StandardJugendC;
    public LigaKosten StandardJugendD;
    public LigaKosten StandardJugendE;
    public Map<String, LigaKosten> Abweichungen = new HashMap<>();

    public static KostenConfiguration loadOrCreate() throws ConfigException {
        KostenConfigurationFile configFile = KostenConfigurationFile.defaultConfigFile();
        if(!configFile.exists()){
            configFile.save(KostenConfiguration.defaultConfig());
        }
        return configFile.load();
    }

    public Schirikosten calculate(SchirieinsatzAblauf ablauf) {

        String trimmedLiga = ablauf.getLigaBezeichnungAusEmail()
                .toLowerCase()
                .replaceAll("gr. \\d", "")
                .trim();
        LigaKosten configForliga = Abweichungen.get(trimmedLiga);
        if(configForliga == null){
            if(trimmedLiga.contains("jugend a")){
                configForliga = StandardJugendA;
            } else if(trimmedLiga.contains("jugend b")){
                configForliga = StandardJugendB;
            } else if(trimmedLiga.contains("jugend c")){
                configForliga = StandardJugendC;
            } else if(trimmedLiga.contains("jugend d")){
                configForliga = StandardJugendD;
            } else if(trimmedLiga.contains("jugend e")){
                configForliga = StandardJugendE;
            } else {
                configForliga = StandardSenioren;
            }
        }
        return new Schirikosten(configForliga, ablauf.getKilometerFahrer(), ablauf.getKilometerBeifahrer());
    }

    private KostenConfiguration(){}

    public void add(String liga, double teilnahmeEntschaedigung, double kilometerpauschaleFahrer, double kilometerpauschaleBeifahrer){
        Abweichungen.put(liga.trim().toLowerCase(), new LigaKosten(teilnahmeEntschaedigung, kilometerpauschaleFahrer, kilometerpauschaleBeifahrer));
    }

    public KostenConfiguration(Map<String, LigaKosten> configRows){
        StandardSenioren = configRows.remove("Standard Senioren");
        StandardJugendA  = configRows.remove("Standard Jugend A");
        StandardJugendB  = configRows.remove("Standard Jugend B");
        StandardJugendC  = configRows.remove("Standard Jugend C");
        StandardJugendD  = configRows.remove("Standard Jugend D");
        StandardJugendE  = configRows.remove("Standard Jugend E");
        Abweichungen = configRows;
    }

    public static final List<String> MANDATORY_KEYS = List.of(
            "Standard Senioren",
            "Standard Jugend A",
            "Standard Jugend B",
            "Standard Jugend C",
            "Standard Jugend D",
            "Standard Jugend E"
    );

    public static KostenConfiguration defaultConfig() {
        KostenConfiguration defaultConfiguration = new KostenConfiguration();

        defaultConfiguration.StandardSenioren = new LigaKosten(22.5, 0.35, 0.05);
        defaultConfiguration.StandardJugendA  = new LigaKosten(22.5, 0.35, 0.05);
        defaultConfiguration.StandardJugendB  = new LigaKosten(20.0, 0.35, 0.05);
        defaultConfiguration.StandardJugendC  = new LigaKosten(20.0, 0.35, 0.05);
        defaultConfiguration.StandardJugendD  = new LigaKosten(16.0, 0.35, 0.05);
        defaultConfiguration.StandardJugendE  = new LigaKosten(16.0, 0.35, 0.05);


        defaultConfiguration.add("Regionalliga Männer                ",80.00,0.30,0.00);
        defaultConfiguration.add("Oberliga Männer                    ",50.00,0.30,0.00);
        defaultConfiguration.add("Verbandsliga Männer                ",40.00,0.30,0.00);
        defaultConfiguration.add("Regionalliga Frauen                ",60.00,0.30,0.00);
        defaultConfiguration.add("Oberliga Frauen                    ",40.00,0.30,0.00);
        defaultConfiguration.add("Verbandsliga Frauen                ",30.00,0.30,0.00);

        defaultConfiguration.add("Regionalliga männliche Jugend A    ",35.00,0.30,0.00);
        defaultConfiguration.add("Oberliga männliche Jugend A        ",30.00,0.30,0.00);
        defaultConfiguration.add("Regionalliga männliche Jugend B    ",30.00,0.30,0.00);
        defaultConfiguration.add("Oberliga männliche Jugend B        ",30.00,0.30,0.00);
        defaultConfiguration.add("Regionalliga männliche Jugend C    ",30.00,0.30,0.00);
        defaultConfiguration.add("Oberliga männliche Jugend C        ",30.00,0.30,0.00);

        defaultConfiguration.add("Regionalliga weibliche Jugend A    ",35.00,0.30,0.00);
        defaultConfiguration.add("Oberliga weibliche Jugend A        ",30.00,0.30,0.00);
        defaultConfiguration.add("Regionalliga weibliche Jugend B    ",30.00,0.30,0.00);
        defaultConfiguration.add("Oberliga weibliche Jugend B        ",30.00,0.30,0.00);
        defaultConfiguration.add("Regionalliga weibliche Jugend C    ",30.00,0.30,0.00);
        defaultConfiguration.add("Oberliga weibliche Jugend C        ",30.00,0.30,0.00);
        defaultConfiguration.add("Regionsoberliga Männer             ",25.00,0.35,0.05);
        defaultConfiguration.add("Regionsoberliga Frauen             ",25.00,0.35,0.05);

        return defaultConfiguration;
    }
}
