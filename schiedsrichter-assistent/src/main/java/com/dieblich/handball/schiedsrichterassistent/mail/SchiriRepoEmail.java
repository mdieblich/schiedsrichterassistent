package com.dieblich.handball.schiedsrichterassistent.mail;

import com.dieblich.handball.schiedsrichterassistent.Schiedsrichter;
import com.dieblich.handball.schiedsrichterassistent.SchiriConfiguration;
import com.dieblich.handball.schiedsrichterassistent.SchiriRepo;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.mail.MessagingException;

import java.util.Optional;

public class SchiriRepoEmail implements SchiriRepo {

    private final EmailServerRead emailServer;

    public SchiriRepoEmail(EmailServerRead emailServer) {
        this.emailServer = emailServer;
    }

    @Override
    public Optional<SchiriConfiguration> findConfigByEmail(String emailAddress) throws SchiriRepoException {
        try {
            Optional<Email> optionalConfigEmail = findConfigEmail(emailAddress);
            if (optionalConfigEmail.isPresent()) {
                Email configEmail = optionalConfigEmail.get();
                return Optional.of(SchiriConfiguration.fromJSON(configEmail.getContent()));
            }
        } catch (Exception e) {
            throw new SchiriRepoException("Fehler beim Lesen der Konfiguration für \"" + emailAddress + "\"", e);
        }
        return Optional.empty();
    }

    private Optional<Email> findConfigEmail(String emailAddress) throws MessagingException {
        EmailFolder schiedsrichter = emailServer.getFolder("SCHIEDSRICHTER");

        for (Email email : schiedsrichter.getEmails()) {
            if (email.isFrom(emailAddress)) {
                return Optional.of(email);
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<SchiriConfiguration> findConfigByName(Schiedsrichter schiedsrichter) throws SchiriRepoException {
        try {
            Optional<Email> optionalConfigEmail = findConfigEmail(schiedsrichter);
            if (optionalConfigEmail.isPresent()) {
                Email configEmail = optionalConfigEmail.get();
                return Optional.of(SchiriConfiguration.fromJSON(configEmail.getContent()));
            }
        } catch (Exception e) {
            throw new SchiriRepoException("Fehler beim Lesen der Konfiguration für \"" + schiedsrichter + "\"", e);
        }
        return Optional.empty();
    }

    private Optional<Email> findConfigEmail(Schiedsrichter schiedsrichter) throws MessagingException {
        EmailFolder schiedsrichterFolder = emailServer.getFolder("SCHIEDSRICHTER");
        String emailSubject = schiedsrichter.nachname() + ", " + schiedsrichter.vorname();

        for (Email email : schiedsrichterFolder.getEmails()) {
            if (email.hasSubject(emailSubject)) {
                return Optional.of(email);
            }
        }
        return Optional.empty();

    }

    @Override
    public void overwriteSchiriConfiguration(SchiriConfiguration config) throws SchiriRepoException {
        try {
            // first, lets search for an old config
            Optional<Email> oldConfigEmail = findConfigEmail(config.Benutzerdaten.Email);

            // then, update
            saveSchiriConfig(config);

            // last - delete the old one
            if (oldConfigEmail.isPresent()) {
                Email oldConfig2 = oldConfigEmail.get();
                oldConfig2.deleteImmediately();
            }
        } catch (Exception e) {
            throw new SchiriRepoException("Konfiguration konnte nicht gespeichert werden: " + config, e);
        }
    }

    private void saveSchiriConfig(SchiriConfiguration config) throws MessagingException, JsonProcessingException {
        EmailFolder schiedsrichter = emailServer.getFolder("SCHIEDSRICHTER");
        Email configEmail = schiedsrichter.prepareEmailForUpload();
        configEmail.setSubject(config.Benutzerdaten.getAnzeigeName());
        configEmail.setFrom(config.Benutzerdaten.Email);
        configEmail.setContent(config.toJSON());
        schiedsrichter.upload(configEmail);
    }
}
