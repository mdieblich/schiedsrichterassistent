package com.dieblich.handball.schiedsrichterassistent.config;

import com.dieblich.handball.schiedsrichterassistent.Schiedsrichter;
import com.dieblich.handball.schiedsrichterassistent.mail.Email;
import com.dieblich.handball.schiedsrichterassistent.mail.EmailException;
import com.dieblich.handball.schiedsrichterassistent.mail.EmailFolder;
import com.dieblich.handball.schiedsrichterassistent.mail.EmailServerRead;
import com.fasterxml.jackson.core.JsonProcessingException;

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

    private Optional<Email> findConfigEmail(String emailAddress) throws EmailException {
        EmailFolder schiedsrichter = emailServer.fetchFolder("SCHIEDSRICHTER");

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

    private Optional<Email> findConfigEmail(Schiedsrichter schiedsrichter) throws EmailException {
        EmailFolder schiedsrichterFolder = emailServer.fetchFolder("SCHIEDSRICHTER");
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

    private void saveSchiriConfig(SchiriConfiguration config) throws JsonProcessingException, EmailException {
        EmailFolder schiedsrichter = emailServer.fetchFolder("SCHIEDSRICHTER");
        Email configEmail = new Email(
                config.Benutzerdaten.Email,
                "nobody@cares.de",
                config.Benutzerdaten.getAnzeigeName(),
                config.toJSON()
                );
        schiedsrichter.upload(configEmail);
    }
}
