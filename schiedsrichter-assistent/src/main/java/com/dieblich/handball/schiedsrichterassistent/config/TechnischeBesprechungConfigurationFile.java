package com.dieblich.handball.schiedsrichterassistent.config;

import org.apache.logging.log4j.util.Strings;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

public class TechnischeBesprechungConfigurationFile {
    public static final String FILE_NAME = "technischeBesprechung.csv";
    private final File file;

    public TechnischeBesprechungConfigurationFile(String filename){
        file = new File(filename);
    }

    public static TechnischeBesprechungConfigurationFile defaultConfigFile(){
        return new TechnischeBesprechungConfigurationFile(FILE_NAME);
    }

    public boolean exists(){
        return file.exists();
    }

    public TechnischeBesprechungConfiguration load() throws ConfigException {
        try {
            List<String> configFileContent  = Files.readAllLines(file.toPath());
            checkErrorsInConfigFile(configFileContent);
            Map<String, Integer> configRows = convertToConfigRows(configFileContent);
            checkMandatoryKeys(configRows);
            TechnischeBesprechungConfiguration loadedConfig = new TechnischeBesprechungConfiguration(configRows);
            System.out.println("Datei mit Dauer der technischen Besprechungen erfolgreich geladen.");
            return loadedConfig;
        } catch (Exception e) {
            throw new ConfigException("Fehler beim Lesen der Datei mit Dauer der technischen Besprechungen: "+ file.getAbsolutePath(), e);
        }
    }

    private void checkErrorsInConfigFile(List<String> configFileContent) throws ConfigException {
        checkFileLength(configFileContent);
        String headerLine = configFileContent.get(0);
        checkHeader(headerLine);
    }

    private void checkFileLength(List<String> configFileContent) throws ConfigException {
        if(configFileContent.size()<2){
            throw new ConfigException("Zu wenige Zeilen, mindestens 2 Zeilen (Header+Inhalt) müssen vorhanden sein.");
        }
    }

    private void checkHeader(String headerLine) throws ConfigException {
        String[] headerLineParts = headerLine.split(";");
        if(headerLineParts.length < 2){
            throw new ConfigException("Zu wenige Spalten im Header, mindestens 2 müssen es sein (Liga;Dauer).");
        }
        List<String> columnErrors = new ArrayList<>();
        if(columnWrong("Liga", headerLineParts[0])){
            columnErrors.add("Spalte \"Liga\" falsch: " + headerLineParts[0]);
        }
        if(columnWrong("Dauer", headerLineParts[1])){
            columnErrors.add("Spalte \"Dauer\" falsch: " + headerLineParts[1]);
        }
        if(!columnErrors.isEmpty()){
            throw new ConfigException("Folgende Spalten sind falsch: " + Strings.join(columnErrors, '\n'));
        }
    }

    private boolean columnWrong(String expectedName, String columnHeader){
        boolean columnCorrect = expectedName.equalsIgnoreCase(columnHeader.trim());
        return !columnCorrect;
    }

    private Map<String, Integer> convertToConfigRows(List<String> csvLines) throws ConfigException {
        Map<String, Integer> configRows = new HashMap<>();
        // skip first row, because it's the header
        for(int i=1; i<csvLines.size(); i++){
            String line = csvLines.get(i);
            try {
                Map.Entry<String, Integer> configRow = convertToConfigRow(line);
                configRows.put(configRow.getKey(), configRow.getValue());
            } catch (ConfigException e) {
                throw new ConfigException("Fehler in Zeile " + (i+1), e);
            }
        }
        return configRows;
    }

    private Map.Entry<String, Integer> convertToConfigRow(String csvLine) throws ConfigException {
        // skip first row, because it's the header
        String[] columns = csvLine.split(";");
        if(columns.length < 2){
            throw new ConfigException("Zu wenig Spalten in der Zeile: " + csvLine);
        }
        String liga = columns[0].trim();
        int dauer = Integer.parseInt(columns[1].trim());
        return new AbstractMap.SimpleEntry<>(liga, dauer);
    }

    private void checkMandatoryKeys(Map<String, Integer> configRows) throws ConfigException {
        List<String> missingMandatoryKeys = new ArrayList<>();
        for(String mandatoryKey:TechnischeBesprechungConfiguration.MANDATORY_KEYS){
            if(!configRows.containsKey(mandatoryKey)){
                missingMandatoryKeys.add(mandatoryKey);
            }
        }
        if(!missingMandatoryKeys.isEmpty()){
            throw new ConfigException("Folgende Zeilen fehlen:\n" + Strings.join(missingMandatoryKeys, '\n'));
        }
    }

    public void save(TechnischeBesprechungConfiguration config) throws ConfigException {
        List<String> configRows = toConfigRows(config);
        try {
            Files.write(file.toPath(), configRows);
        } catch (IOException e) {
            throw new ConfigException("Fehler beim Schreiben der Datei mit Dauer der technischen Besprechungen: "+ file.getAbsolutePath(), e);
        }
        System.out.println("Datei mit Dauer der technischen Besprechungen neu angelegt.");
    }

    private List<String> toConfigRows(TechnischeBesprechungConfiguration config) {
        List<String> configRows = new ArrayList<>();
        configRows.add(HEADER_ROW);
        configRows.add(toConfigRow("Standard", config.standard));
        config.abweichungen.forEach((key, value) -> configRows.add(toConfigRow(key, value)));
        return configRows;
    }

    public static final String HEADER_ROW = "Liga;Dauer";

    private String toConfigRow(String name, int eintrag) {
        return name + ";" + eintrag;
    }
}
