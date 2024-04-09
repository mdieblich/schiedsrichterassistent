package com.dieblich.handball.schiedsrichterassistent.mail;

import com.dieblich.handball.schiedsrichterassistent.Schiedsrichter;
import com.dieblich.handball.schiedsrichterassistent.SchiriConfiguration;
import com.dieblich.handball.schiedsrichterassistent.SchiriRepo;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
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
    public void createsNewConfig() throws MessagingException, IOException, SchiriRepo.SchiriRepoException {
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
        assertEquals(Optional.of("max@mustermann.com"), firstEmail.getFrom());
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
                  "Gespannpartner" : null
                }""", firstEmail.getContent().replace("\r", ""));
    }
    @Test
    public void overwritesConfig() throws MessagingException, IOException, SchiriRepo.SchiriRepoException {
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
        assertEquals(Optional.of("max@mustermann.com"), firstEmail.getFrom());
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
                  "Gespannpartner" : null
                }""", firstEmail.getContent().replace("\r", ""));
    }

    @Test
    public void configStatusNEW() throws SchiriRepo.SchiriRepoException {

        // arrange
        EmailServerReadFake fakeEmailServer = new EmailServerReadFake();
        fakeEmailServer.createFolder("SCHIEDSRICHTER");
        // empty folder
        SchiriRepoEmail repo = new SchiriRepoEmail(fakeEmailServer);

        // act
        SchiriRepo.ConfigurationStatus configSatus = repo.getConfigurationStatus("max@mustermann.com");

        // assert
        assertEquals(SchiriRepo.ConfigurationStatus.NEW, configSatus);
    }
    @Test
    public void configStatusINCOMPLETE() throws SchiriRepo.SchiriRepoException {

        // arrange
        EmailServerReadFake fakeEmailServer = new EmailServerReadFake();
        EmailFolderFake schiedsrichterFolder = fakeEmailServer.createFolder("SCHIEDSRICHTER");
        schiedsrichterFolder.createEmail("max@mustermann.com", "Mustermann, Max", "{\"Benutzerdaten\":{\"Adresse\":\"irgendwo\"}}");
        SchiriRepoEmail repo = new SchiriRepoEmail(fakeEmailServer);

        // act
        SchiriRepo.ConfigurationStatus configSatus = repo.getConfigurationStatus("max@mustermann.com");

        // assert
        assertEquals(SchiriRepo.ConfigurationStatus.INCOMPLETE, configSatus);
    }
    @Test
    public void configStatusCOMPLETE() throws SchiriRepo.SchiriRepoException {

        // arrange
        EmailServerReadFake fakeEmailServer = new EmailServerReadFake();
        EmailFolderFake schiedsrichterFolder = fakeEmailServer.createFolder("SCHIEDSRICHTER");
        schiedsrichterFolder.createEmail("max@mustermann.com", "Mustermann, Max", """
        {
          "Benutzerdaten" : {
            "Email" : "max@mustermann.com",
            "Vorname" : "Max",
            "Nachname" : "Mustermann",
            "Adresse" : "Musterstr. 17, 54321 Köln",
            "Längengrad" : 1.23456,
            "Breitengrad" : 5.67891
          },
          "Spielablauf" : {
            "EffektiveSpielDauer" : 90,
            "UmziehenVorSpiel" : 15,
            "PapierKramNachSpiel" : 15,
            "UmziehenNachSpiel" : 15,
            "TechnischeBesprechung" : {
              "StandardDauerInMinuten" : 30,
              "Abweichungen" : {
                "Oberliga" : 45,
                "Regionalliga" : 45
              }
            }
          },
          "Gespannpartner": [
            "mike.blind@loser.com"
          ]
        }""");
        SchiriRepoEmail repo = new SchiriRepoEmail(fakeEmailServer);

        // act
        SchiriRepo.ConfigurationStatus configSatus = repo.getConfigurationStatus("max@mustermann.com");

        // assert
        assertEquals(SchiriRepo.ConfigurationStatus.COMPLETE, configSatus);
    }
}