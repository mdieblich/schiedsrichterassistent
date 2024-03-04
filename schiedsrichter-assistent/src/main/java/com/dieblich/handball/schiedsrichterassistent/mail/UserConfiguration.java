package com.dieblich.handball.schiedsrichterassistent.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.Session;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class UserConfiguration{

    private static final Map<String, String> DEFAULT_CONFIG = Map.ofEntries(
            new AbstractMap.SimpleEntry<>("Umziehen.DauerInMinuten", "15"),
            new AbstractMap.SimpleEntry<>("TechnischeBesprechung.Oberliga.DauerInMinuten", "45"),
            new AbstractMap.SimpleEntry<>("TechnischeBesprechung.UnterOberliga.DauerInMinuten", "30")
    );

    private static final Set<String> MANDATORY_CONFIG = Set.of(
            "Adresse"
    );

    private static final Set<String> OPTIONAL_CONFIG = Set.of(
            "Adresse.GeoLocation"
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

    private String configToString() {
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

    public void updateWith(String configUpdate) {
        List<String> validLines = extractFirstValidLines(configUpdate);
        Map<String, String> keyValuePairs = toKeyValuePairs(validLines);
        Map<String, String> allowedKeyValuePairs = keyValuePairs.entrySet().stream()
                .filter(entry -> ALL_CONFIG_KEYS.contains(entry.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        //das hier mal testen
        configuration.putAll(allowedKeyValuePairs);
    }

    private List<String> extractFirstValidLines(String content){
        String[] allLines = content.split("\\r?\\n|\\r");
        List<String> validLines = new ArrayList<>();
        for (String line: allLines) {
            String trimmedLine = line.trim();
            if (trimmedLine.isEmpty()){
                // ignore Empty lines
                continue;
            } else if(trimmedLine.matches("([\\w\\d.]*) *= *(.*)")){
                // this is a config-line "config.key = some value"
                validLines.add(trimmedLine);
            } else {
                // stop if there are strange lines
                break;
            }
        }
        return validLines;
    }

    private Map<String, String> toKeyValuePairs(List<String> lines){
        Map<String, String> keyValuePairs = new HashMap<>();
        for (String line:lines) {
            String[] lineParts = line.split("=");
            if(lineParts.length != 2){
                continue;
            }
            String key = lineParts[0];
            String value = lineParts[1];
            keyValuePairs.put(key.trim(), value.trim());
        }
        return keyValuePairs;
    }

    public int size() {
        return configuration.size();
    }

    public Optional<String> get(String key) {
        String value = configuration.getProperty(key);
        return Optional.ofNullable(value);
    }
}