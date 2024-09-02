package com.dieblich.handball.schiedsrichterassistent.mail;

import com.dieblich.handball.schiedsrichterassistent.Schiedsrichter;
import com.dieblich.handball.schiedsrichterassistent.config.SchiriConfiguration;
import com.dieblich.handball.schiedsrichterassistent.config.SchiriRepo;
import com.dieblich.handball.schiedsrichterassistent.config.SchiriRepoEmail;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class SchiriRepoEmailTest {

    @Test
    public void findByEmailReturnsEmpty() throws SchiriRepo.SchiriRepoException {
        // arrange
        EmailServerReadFake fakeEmailServer = new EmailServerReadFake();
        fakeEmailServer.createFolder("SCHIEDSRICHTER");
        // empty folder
        SchiriRepoEmail repo = new SchiriRepoEmail(fakeEmailServer);

        // act
        Optional<SchiriConfiguration> optionalConfig = repo.findConfigByEmail("max@mustermann.com");

        // assert
        assertTrue(optionalConfig.isEmpty());
    }

    @Test
    public void findByEmailSearchesByEmail() throws SchiriRepo.SchiriRepoException {
        // arrange
        EmailServerReadFake fakeEmailServer = new EmailServerReadFake();
        EmailFolderFake schiedsrichterFolder = fakeEmailServer.createFolder("SCHIEDSRICHTER");
        schiedsrichterFolder.createEmail("max@mustermann.com", "Mustermann, Max", "{\"Benutzerdaten\":{\"Vorname\":\"Max\"}}");
        SchiriRepoEmail repo = new SchiriRepoEmail(fakeEmailServer);

        // act
        Optional<SchiriConfiguration> optionalConfig = repo.findConfigByEmail("max@mustermann.com");

        // assert
        assertTrue(optionalConfig.isPresent());
        assertEquals("Max", optionalConfig.get().Benutzerdaten.Vorname);
    }
    @Test
    public void findByEmailSearchesByName() throws SchiriRepo.SchiriRepoException {
        // arrange
        EmailServerReadFake fakeEmailServer = new EmailServerReadFake();
        EmailFolderFake schiedsrichterFolder = fakeEmailServer.createFolder("SCHIEDSRICHTER");
        schiedsrichterFolder.createEmail("max@mustermann.com", "Mustermann, Max", "{\"Benutzerdaten\":{\"Vorname\":\"Max\"}}");
        SchiriRepoEmail repo = new SchiriRepoEmail(fakeEmailServer);

        // act
        Optional<SchiriConfiguration> optionalConfig = repo.findConfigByName(new Schiedsrichter("Max", "Mustermann"));

        // assert
        assertTrue(optionalConfig.isPresent());
        assertEquals("Max", optionalConfig.get().Benutzerdaten.Vorname);
    }

    @Test
    public void createsNewConfig() throws SchiriRepo.SchiriRepoException {
        // arrange
        EmailServerReadFake fakeEmailServer = new EmailServerReadFake();
        fakeEmailServer.createFolder("SCHIEDSRICHTER");
        SchiriRepoEmail repo = new SchiriRepoEmail(fakeEmailServer);

        SchiriConfiguration configuration = new SchiriConfiguration();
        configuration.Benutzerdaten.Email = "max@mustermann.com";
        configuration.Benutzerdaten.Vorname = "Max";
        configuration.Benutzerdaten.Nachname = "Mustermann";
        configuration.Spielablauf = null;
        configuration.Gespannpartner = null;

        // act
        repo.overwriteSchiriConfiguration(configuration);

        // assert
        EmailFolder folder = fakeEmailServer.fetchFolder("SCHIEDSRICHTER");
        List<Email> emails = folder.getEmails();
        assertEquals(1, emails.size());
        Email firstEmail = emails.get(0);
        assertEquals("max@mustermann.com", firstEmail.getSender());
        assertEquals("Mustermann, Max", firstEmail.getSubject());
        assertEquals("""
                {
                  "Benutzerdaten" : {
                    "Email" : "max@mustermann.com",
                    "Vorname" : "Max",
                    "Nachname" : "Mustermann",
                    "Adresse" : null
                  },
                  "Spielablauf" : null,
                  "Gespannpartner" : null,
                  "Kosten" : {
                    "TeilnahmeEntschädigung" : {
                      "Standard" : null,
                      "Abweichungen" : { }
                    },
                    "Fahrer" : {
                      "Standard" : null,
                      "Abweichungen" : { }
                    },
                    "Beifahrer" : {
                      "Standard" : null,
                      "Abweichungen" : { }
                    }
                  }
                }""", firstEmail.getContent().replace("\r", ""));
    }
    @Test
    public void overwritesConfig() throws SchiriRepo.SchiriRepoException {
        // arrange
        EmailServerReadFake fakeEmailServer = new EmailServerReadFake();
        EmailFolderFake schiedsrichterFolder = fakeEmailServer.createFolder("SCHIEDSRICHTER");
        schiedsrichterFolder.createEmail("max@mustermann.com", "Mustermann, Max", "{\"Benutzerdaten\":{\"Adresse\":\"irgendwo\"}}");

        SchiriRepoEmail repo = new SchiriRepoEmail(fakeEmailServer);

        SchiriConfiguration configuration = new SchiriConfiguration();
        configuration.Benutzerdaten.Email = "max@mustermann.com";
        configuration.Benutzerdaten.Vorname = "Max";
        configuration.Benutzerdaten.Nachname = "Mustermann";
        configuration.Spielablauf = null;
        configuration.Gespannpartner = null;

        // act
        repo.overwriteSchiriConfiguration(configuration);

        // assert
        EmailFolder folder = fakeEmailServer.fetchFolder("SCHIEDSRICHTER");
        List<Email> emails = folder.getEmails();
        assertEquals(1, emails.size());
        Email firstEmail = emails.get(0);
        assertEquals("max@mustermann.com", firstEmail.getSender());
        assertEquals("Mustermann, Max", firstEmail.getSubject());
        assertEquals("""
                {
                  "Benutzerdaten" : {
                    "Email" : "max@mustermann.com",
                    "Vorname" : "Max",
                    "Nachname" : "Mustermann",
                    "Adresse" : null
                  },
                  "Spielablauf" : null,
                  "Gespannpartner" : null,
                  "Kosten" : {
                    "TeilnahmeEntschädigung" : {
                      "Standard" : null,
                      "Abweichungen" : { }
                    },
                    "Fahrer" : {
                      "Standard" : null,
                      "Abweichungen" : { }
                    },
                    "Beifahrer" : {
                      "Standard" : null,
                      "Abweichungen" : { }
                    }
                  }
                }""", firstEmail.getContent().replace("\r", ""));
    }
}