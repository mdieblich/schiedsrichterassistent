package com.dieblich.handball.schiedsrichterassistent.config;

import org.apache.logging.log4j.util.Strings;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;

public class KostenConfigurationFile {
    public static final String FILE_NAME = "kosten.csv";
    private final File file;

    public KostenConfigurationFile(String filename){
        file = new File(filename);
    }

    public static KostenConfigurationFile defaultConfigFile(){
        return new KostenConfigurationFile(FILE_NAME);
    }

    public boolean exists(){
        return file.exists();
    }

    public KostenConfiguration load() throws ConfigException {
        try {
            List<String> configFileContent  = Files.readAllLines(file.toPath());
            checkErrorsInConfigFile(configFileContent);
            Map<String, KostenConfiguration.KostenConfigurationsEintrag> configRows = convertToConfigRows(configFileContent);
            checkMandatoryKeys(configRows);
            return new KostenConfiguration(configRows);
        } catch (Exception e) {
            throw new ConfigException("Fehler beim Lesen der Kosten-Datei: "+ file.getAbsolutePath(), e);
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
        if(headerLineParts.length < 4){
            throw new ConfigException("Zu wenige Spalten im Header, mindestens 4 müssen es sein (Liga;Teilnahmeentschädigung;KilometerpauschaleFahrer;KilometerpauschaleBeiFahrer).");
        }
        List<String> columnErrors = new ArrayList<>();
        if(columnWrong("Liga", headerLineParts[0])){
            columnErrors.add("Spalte \"Liga\" falsch: " + headerLineParts[0]);
        }
        if(columnWrong("Teilnahmeentschädigung", headerLineParts[1])){
            columnErrors.add("Spalte \"Teilnahmeentschädigung\" falsch: " + headerLineParts[1]);
        }
        if(columnWrong("KilometerpauschaleFahrer", headerLineParts[2])){
            columnErrors.add("Spalte \"KilometerpauschaleFahrer\" falsch: " + headerLineParts[2]);
        }
        if(columnWrong("KilometerpauschaleBeiFahrer", headerLineParts[3])){
            columnErrors.add("Spalte \"KilometerpauschaleBeiFahrer\" falsch: " + headerLineParts[3]);
        }
        if(!columnErrors.isEmpty()){
            throw new ConfigException("Folgende Spalten sind falsch: " + Strings.join(columnErrors, '\n'));
        }
    }

    private boolean columnWrong(String expectedName, String columnHeader){
        boolean columnCorrect = expectedName.equalsIgnoreCase(columnHeader.trim());
        return !columnCorrect;
    }

    private Map<String, KostenConfiguration.KostenConfigurationsEintrag> convertToConfigRows(List<String> csvLines) throws ConfigException {
        Map<String, KostenConfiguration.KostenConfigurationsEintrag> configRows = new HashMap<>();
        // skip first row, because it's the header
        for(int i=1; i<csvLines.size(); i++){
            String line = csvLines.get(i);
            try {
                Map.Entry<String, KostenConfiguration.KostenConfigurationsEintrag> configRow = convertToConfigRow(line);
                configRows.put(configRow.getKey(), configRow.getValue());
            } catch (ConfigException e) {
                throw new ConfigException("Fehler in Zeile " + (i+1), e);
            }
        }
        return configRows;
    }

    private Map.Entry<String, KostenConfiguration.KostenConfigurationsEintrag> convertToConfigRow(String csvLine) throws ConfigException {
        // skip first row, because it's the header
        String[] columns = csvLine.split(";");
        if(columns.length < 4){
            throw new ConfigException("Zu wenig Spalten in der Zeile: " + csvLine);
        }
        String liga = columns[0].trim();
        KostenConfiguration.KostenConfigurationsEintrag configRow = new KostenConfiguration.KostenConfigurationsEintrag(
                Double.parseDouble(columns[1].trim()),
                Double.parseDouble(columns[2].trim()),
                Double.parseDouble(columns[3].trim())
        );
        return new AbstractMap.SimpleEntry<>(liga, configRow);
    }

    private void checkMandatoryKeys(Map<String, KostenConfiguration.KostenConfigurationsEintrag> configRows) throws ConfigException {
        List<String> missingMandatoryKeys = new ArrayList<>();
        for(String mandatoryKey:KostenConfiguration.MANDATORY_KEYS){
            if(!configRows.containsKey(mandatoryKey)){
                missingMandatoryKeys.add(mandatoryKey);
            }
        }
        if(!missingMandatoryKeys.isEmpty()){
            throw new ConfigException("Folgende Zeilen fehlen:\n" + Strings.join(missingMandatoryKeys, '\n'));
        }
    }

    public void save(KostenConfiguration config) throws ConfigException {
        List<String> configRows = toConfigRows(config);
        try {
            Files.write(file.toPath(), configRows);
        } catch (IOException e) {
            throw new ConfigException("Fehler beim Schreiben der Kosten-Datei: "+ file.getAbsolutePath(), e);
        }
    }

    private List<String> toConfigRows(KostenConfiguration config) {
        List<String> configRows = new ArrayList<>();
        configRows.add(HEADER_ROW);
        configRows.add(toConfigRow("Standard Senioren", config.StandardSenioren));
        configRows.add(toConfigRow("Standard Jugend A", config.StandardJugendA));
        configRows.add(toConfigRow("Standard Jugend B", config.StandardJugendB));
        configRows.add(toConfigRow("Standard Jugend C", config.StandardJugendC));
        configRows.add(toConfigRow("Standard Jugend D", config.StandardJugendD));
        configRows.add(toConfigRow("Standard Jugend E", config.StandardJugendE));
        config.Abweichungen.forEach((key, value) -> configRows.add(toConfigRow(key, value)));
        return configRows;
    }

    public static final String HEADER_ROW = "Liga;Teilnahmeentschädigung;KilometerpauschaleFahrer;KilometerpauschaleBeiFahrer";

    private String toConfigRow(String name, KostenConfiguration.KostenConfigurationsEintrag eintrag) {
        DecimalFormat df = new DecimalFormat("0.00",new DecimalFormatSymbols(Locale.US));
        return name + ";" +
                df.format(eintrag.Teilnahmeentschädigung) + ";" +
                df.format(eintrag.KilometerpauschaleFahrer) + ";" +
                df.format(eintrag.KilometerpauschaleBeiFahrer);
    }
}
