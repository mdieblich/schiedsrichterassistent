package com.dieblich.handball.schiedsrichterassistent.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.Session;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;

public class UserConfiguration{

    // TODO in Sub-Record-Klassen auslagern
    public static final String UMZIEHEN_DAUER_IN_MINUTEN = "Umziehen.DauerInMinuten";


    public static final String TECHNISCHE_BESPRECHUNG = "TechnischeBesprechung";
    public static final String DAUER_IN_MINUTEN = "DauerInMinuten";
    public static final String TECHNISCHE_BESPRECHUNG_REGIONALLIGA_DAUER_IN_MINUTEN = TECHNISCHE_BESPRECHUNG+".Regionalliga."+DAUER_IN_MINUTEN;
    public static final String TECHNISCHE_BESPRECHUNG_OBERLIGA_DAUER_IN_MINUTEN = TECHNISCHE_BESPRECHUNG+".Oberliga."+DAUER_IN_MINUTEN;
    public static final String TECHNISCHE_BESPRECHUNG_ANDERE_LIGEN_DAUER_IN_MINUTEN = TECHNISCHE_BESPRECHUNG+".AndereLigen."+DAUER_IN_MINUTEN;

    public static final String SCHIRI_VORNAME = "Schiri.Vorname";
    public static final String SCHIRI_NACHNAME = "Schiri.Nachname";
    public static final String SCHIRI_ADRESSE = "Schiri.Adresse";
    public static final String SCHIRI_GEOLOCATION = "Schiri.GeoLocation";

    private static final Map<String, String> DEFAULT_CONFIG = Map.ofEntries(
            new AbstractMap.SimpleEntry<>(UMZIEHEN_DAUER_IN_MINUTEN, "15"),
            new AbstractMap.SimpleEntry<>(TECHNISCHE_BESPRECHUNG_REGIONALLIGA_DAUER_IN_MINUTEN, "45"),
            new AbstractMap.SimpleEntry<>(TECHNISCHE_BESPRECHUNG_OBERLIGA_DAUER_IN_MINUTEN, "45"),
            new AbstractMap.SimpleEntry<>(TECHNISCHE_BESPRECHUNG_ANDERE_LIGEN_DAUER_IN_MINUTEN, "30")
    );

    private static final Set<String> MANDATORY_CONFIG = Set.of(
            SCHIRI_VORNAME,
            SCHIRI_NACHNAME,
            SCHIRI_ADRESSE
    );

    private static final Set<String> OPTIONAL_CONFIG = Set.of(
            SCHIRI_GEOLOCATION
    );

    private static final Set<String> ALL_CONFIG_KEYS;// = getAllConfigKeys();

    static {
        ALL_CONFIG_KEYS = new HashSet<>();
        ALL_CONFIG_KEYS.addAll(DEFAULT_CONFIG.keySet());
        ALL_CONFIG_KEYS.addAll(MANDATORY_CONFIG);
        ALL_CONFIG_KEYS.addAll(OPTIONAL_CONFIG);
    }

    private final String userEmail;
    private final Properties configuration = new Properties();

    public UserConfiguration(String userEmail, String content) throws IOException {
        this(userEmail);
        configuration.load(new StringReader(content));
    }

    public static UserConfiguration DEFAULT(String userEmail){
        UserConfiguration config = new UserConfiguration(userEmail);
        config.configuration.putAll(DEFAULT_CONFIG);
        return config;
    }

    private UserConfiguration(String userEmail){
        this.userEmail = userEmail;
    }

    public Email toEmail(Session session) throws MessagingException {
        Email email = new Email(session);
        email.setSubject("Konfiguration vom " + now());
        email.setFrom(userEmail);
        email.setContent(configToString());
        return email;
    }

    private String now(){
        Date now = new Date();
        return new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(now);
    }

    public String configToString() {
        try {
            StringWriter writer = new StringWriter();
            configuration.store(new PrintWriter(writer), null);
            return writer.getBuffer().toString();
        } catch (IOException e) {
            throw new RuntimeException("Schreiben der Konfig in den String schlug fehl", e);
        }
    }

    @Override
    public String toString() {
        return "User: " + userEmail + "\n" +
                "Anzahl Einträge: " + configuration.size() + "\n" +
                configToString();
    }

    public void updateWith(Map<String, String> propertiesUpdate) {
        configuration.putAll(propertiesUpdate);
    }

    public String getEmail() {
        return userEmail;
    }

    public void updateWith(String configUpdate, Function<String, Optional<String>> addressToGeoLocation) {
        UserLog notUsed = new UserLog();
        updateWith(configUpdate, addressToGeoLocation, notUsed);
    }

    public void updateWith(String configUpdate, Function<String, Optional<String>> addressToGeoLocation, UserLog log) {
        List<String> validLines = extractFirstValidLines(configUpdate, log);
        Map<String, String> keyValuePairs = toKeyValuePairs(validLines, log);
        Map<String, String> allowedKeyValuePairs = filterAllowedKeys(keyValuePairs, log);

        if(addressIsNew(allowedKeyValuePairs)){
            Optional<String> optionalGeoLocation = addressToGeoLocation.apply(allowedKeyValuePairs.get(SCHIRI_ADRESSE));
            if(optionalGeoLocation.isEmpty()){
                allowedKeyValuePairs.remove(SCHIRI_ADRESSE);
                log.log("Die neue Adresse wird nicht übernommen, da der Breiten- und Längengrad bestimmt werden konnte.");
                log.log("FALLS DAS PROBLEM WIEDERHOLT AUFTRITT SO KANNST DU FOLGENDES TUN:");
                log.log("1. Bestimme mithilfe eines Kartendienstes (z.B. https://www.gpskoordinaten.de/) deinen Längen- und Breitengrad.");
                log.log("2. Sende mir eine Konfigurationsemail mit der Zeile \"Schiri.GeoLocation=6.9582,50.9411\"");
                log.log("   Beachte bitte, dass der Längengrad zuerst angegeben werden muss und dass du min. 4-Nachkommastellen verwendest.");
                log.log("   Verwendet wird das Koordinatensystem WGS 84");

            } else {
                allowedKeyValuePairs.put(SCHIRI_GEOLOCATION, optionalGeoLocation.get());
                log.log("Erfolgreich neue Koordination übernommen: " + optionalGeoLocation.get());
            }
        }

        configuration.putAll(allowedKeyValuePairs);
    }

    private static Map<String, String> filterAllowedKeys(Map<String, String> keyValuePairs, UserLog log) {
        Map<String, String> validKeyValuePairs = new HashMap<>();
        for(Map.Entry<String, String> entry:keyValuePairs.entrySet()){
            if(ALL_CONFIG_KEYS.contains(entry.getKey())){
                validKeyValuePairs.put(entry.getKey(), entry.getValue());
            } else {
                log.log("Eintrag "+entry.getKey()+"="+entry.getValue()+" wird ignoriert, "+
                        "da es keine bekannte Konfiugurationsoption ist.");
            }
        }
        return validKeyValuePairs;
    }


    private List<String> extractFirstValidLines(String content, UserLog log){
        String[] allLines = content.split("\\r?\\n|\\r");
        List<String> validLines = new ArrayList<>();
        for (String line: allLines) {
            String trimmedLine = line.trim();
            if (trimmedLine.isEmpty()){
                // ignore Empty lines
                //noinspection UnnecessaryContinue
                continue;
            } else if(trimmedLine.matches("([\\w.]*) *= *(.*)")){
                // this is a config-line "config.key = some value"
                validLines.add(trimmedLine);
            } else {
                log.log("Stoppe das Interpretieren der Konfiguration ab Zeile >>"+trimmedLine+"<<");
                // stop if there are strange lines
                break;
            }
        }
        return validLines;
    }

    private Map<String, String> toKeyValuePairs(List<String> lines, UserLog log){
        Map<String, String> keyValuePairs = new HashMap<>();
        for (String line:lines) {
            String[] lineParts = line.split("=");
            if(lineParts.length != 2){
                log.log("Die Zeile >>"+line+"<< wird ignoriert, da sie nicht dem Format \"ABC=XYZ\" entspricht");
                continue;
            }
            String key = lineParts[0];
            String value = lineParts[1];
            keyValuePairs.put(key.trim(), value.trim());
        }
        return keyValuePairs;
    }
    boolean addressIsNew(Map<String, String> newConfig) {
        String oldAddress = configuration.getProperty(SCHIRI_ADRESSE);
        String newAddress = newConfig.get(SCHIRI_ADRESSE);
        if(newAddress == null){
            return false;
        }
        else if(oldAddress == null){
            return true;
        }
        return !newAddress.equals(oldAddress);
    }

    public List<String> getMissingConfigKeys(){
        List<String> missingConfigKeys = new ArrayList<>();

        for(String mandatoryKey:MANDATORY_CONFIG){
            if(!configuration.containsKey(mandatoryKey)){
                missingConfigKeys.add(mandatoryKey);
            }
        }
        return missingConfigKeys;
    }
    public boolean isComplete(){
        return getMissingConfigKeys().isEmpty();
    }

    public int size() {
        return configuration.size();
    }

    public Optional<String> get(String key) {
        String value = configuration.getProperty(key);
        return Optional.ofNullable(value);
    }

    public Optional<Integer> getTechnischeBesprechung(String liga) {
        Optional<String> optionalLigaName = findLigaName(liga);
        if(optionalLigaName.isEmpty()){
            return Optional.empty();
        }

        String dauerString = configuration.getProperty(TECHNISCHE_BESPRECHUNG+"."+optionalLigaName.get()+"."+DAUER_IN_MINUTEN);
        if(dauerString == null){
            dauerString = configuration.getProperty(TECHNISCHE_BESPRECHUNG_ANDERE_LIGEN_DAUER_IN_MINUTEN);
        }
        if(dauerString == null){
            return Optional.empty();
        }

        try{
            int dauer = Integer.parseInt(dauerString);
            return Optional.of(dauer);
        } catch(NumberFormatException e){
            return Optional.empty();
        }
    }

    public static Optional<String> findLigaName(String liga){
        String[] ligaParts = liga.split(" ");
        for(String ligaPart:ligaParts){
            String lowerCaseliga = ligaPart.toLowerCase();
            if(lowerCaseliga.contains("liga") || lowerCaseliga.contains("klasse")){
                return Optional.of(ligaPart);
            }
        }
        return Optional.empty();
    }
}
