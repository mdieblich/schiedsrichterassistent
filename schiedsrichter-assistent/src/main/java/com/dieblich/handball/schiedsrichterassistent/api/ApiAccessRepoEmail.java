package com.dieblich.handball.schiedsrichterassistent.api;

import com.dieblich.handball.schiedsrichterassistent.mail.Email;
import com.dieblich.handball.schiedsrichterassistent.mail.EmailFolder;
import com.dieblich.handball.schiedsrichterassistent.mail.EmailServerRead;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

public class ApiAccessRepoEmail implements ApiAccessRepo{

    private final EmailServerRead emailServer;

    public ApiAccessRepoEmail(EmailServerRead emailServer) {
        this.emailServer = emailServer;
    }

    @Override
    public String createAccessKey(String emailAddress) throws ApiAccessRepoException {
        try {
            EmailFolder accessKeyFolder = emailServer.fetchFolder("ACCESS_KEYS");
            Date now = new Date();
            UUID uuid = UUID.randomUUID();
            String key = uuid.toString();

            Email accessEmail = new Email(
                    emailAddress,
                    "nobody@cares.de",
                    now.toString(),
                    key
            );
            accessKeyFolder.upload(accessEmail);
            return key;
        } catch(Exception e) {
            throw new ApiAccessRepoException("Could not create access key for " + emailAddress, e);
        }
    }

    @Override
    public boolean hasAccess(String emailAddress, String key) throws ApiAccessRepoException {
        try {
            EmailFolder accessKeyFolder = emailServer.fetchFolder("ACCESS_KEYS");
            Optional<Email> accessEmail = accessKeyFolder.getEmails().stream().filter(email -> email.isFrom(emailAddress)).findAny();
            //noinspection OptionalIsPresent
            if(accessEmail.isEmpty()){
                return false;
            }

            return accessEmail.get().getContent().equals(key);
        } catch(Exception e) {
            throw new ApiAccessRepoException("Could not validate access key for " + emailAddress, e);
        }
    }
}
