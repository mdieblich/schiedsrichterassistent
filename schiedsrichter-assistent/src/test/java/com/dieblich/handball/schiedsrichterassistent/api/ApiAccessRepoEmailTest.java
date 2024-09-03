package com.dieblich.handball.schiedsrichterassistent.api;

import com.dieblich.handball.schiedsrichterassistent.mail.Email;
import com.dieblich.handball.schiedsrichterassistent.mail.EmailFake;
import com.dieblich.handball.schiedsrichterassistent.mail.EmailFolderFake;
import com.dieblich.handball.schiedsrichterassistent.mail.EmailServerReadFake;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ApiAccessRepoEmailTest {

    @Test
    public void createAccessKey_creates_accessEmail() throws ApiAccessRepo.ApiAccessRepoException {
        // arrange
        EmailServerReadFake fakeEmailServer = new EmailServerReadFake();
        EmailFolderFake accessKeyFolder = fakeEmailServer.createFolder("ACCESS_KEYS");
        ApiAccessRepoEmail accessRepo = new ApiAccessRepoEmail(fakeEmailServer);

        // act
        String accessKey = accessRepo.createAccessKey("martin@wurst.de");

        // assert
        Optional<Email> createdEmail = accessKeyFolder.getEmails().stream().findFirst();
        assertTrue(createdEmail.isPresent());
        assertEquals(accessKey, createdEmail.get().getContent());
    }

    @Test
    public void hasAccess_deniesAccess() throws ApiAccessRepo.ApiAccessRepoException {
        // arrange
        EmailServerReadFake fakeEmailServer = new EmailServerReadFake();
        fakeEmailServer.createFolder("ACCESS_KEYS");
        ApiAccessRepoEmail accessRepo = new ApiAccessRepoEmail(fakeEmailServer);

        // act
        String realAccessKey = accessRepo.createAccessKey("martin@wurst.de");

        // assert
        assertFalse(accessRepo.hasAccess("martin@wurst.de",realAccessKey+"X"));
    }
    @Test
    public void hasAccess_grantsAccess() throws ApiAccessRepo.ApiAccessRepoException {
        // arrange
        EmailServerReadFake fakeEmailServer = new EmailServerReadFake();
        fakeEmailServer.createFolder("ACCESS_KEYS");
        ApiAccessRepoEmail accessRepo = new ApiAccessRepoEmail(fakeEmailServer);

        // act
        String realAccessKey = accessRepo.createAccessKey("martin@wurst.de");

        // assert
        assertTrue(accessRepo.hasAccess("martin@wurst.de", realAccessKey));
    }

    @Test
    public void hasAccess_deniesAccess_afterExpiration() throws ApiAccessRepo.ApiAccessRepoException {
        // arrange
        EmailServerReadFake fakeEmailServer = new EmailServerReadFake();
        EmailFolderFake accessKeyFolder = fakeEmailServer.createFolder("ACCESS_KEYS");
        ApiAccessRepoEmail accessRepo = new ApiAccessRepoEmail(fakeEmailServer);

        // act
        String realAccessKey = accessRepo.createAccessKey("martin@wurst.de");
        Optional<Email> createdEmail = accessKeyFolder.getEmails().stream().findFirst();
        assertTrue(createdEmail.isPresent(), "Precondition failed: Access-Email was not created");
        Email email = createdEmail.get();
        EmailFake artificiallyOlderEmail = accessKeyFolder.createEmail(email.getSender(), email.getSubject(), email.getContent());
        artificiallyOlderEmail.setSentDate(email.getSentDate().minusMinutes(15));
        accessKeyFolder.delete(email);

        // assert
        assertFalse(accessRepo.hasAccess("martin@wurst.de",realAccessKey));
    }

}