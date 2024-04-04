package com.dieblich.handball.schiedsrichterassistent.mail;

import com.dieblich.handball.schiedsrichterassistent.Schiedsrichter;
import com.dieblich.handball.schiedsrichterassistent.SchiriConfiguration;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.mail.*;

import java.io.IOException;
import java.util.Optional;
import java.util.Properties;

public class EmailServerReadImpl implements AutoCloseable, EmailServerRead {

    private final Session session;
    private Store store;
    private final String host;
    private final int port;
    private final String user;
    private final String password;

    public EmailServerReadImpl(String host, int port, String user, String password) {
        this.host = host;
        this.port = port;
        this.user = user;
        this.password = password;
        Properties props = System.getProperties();
        props.put("mail.imap.ssl.enable", true);
        session = Session.getInstance(props, null);
    }

    private void ensureConnection() throws MessagingException {
        if (store == null) {
            store = session.getStore("imap");
        }
        if (!store.isConnected()) {
            store.connect(host, port, user, password);
        }

    }

    @Override
    public EmailFolder getFolder(String name) throws MessagingException {
        ensureConnection();
        Folder defaultFolder = store.getDefaultFolder();
        Folder folder = defaultFolder.getFolder(name);
        if (!folder.exists()) {
            folder.create(Folder.HOLDS_FOLDERS | Folder.HOLDS_MESSAGES);
        }
        folder.open(Folder.READ_WRITE);
        return new EmailFolderImpl(folder, session);
    }

    public SchiriConfiguration loadSchiriConfiguration(String emailAddress) throws IOException, MessagingException {
        Optional<Email> email = findConfigEmail(emailAddress);
        if(email.isEmpty()){
            return SchiriConfiguration.NEW_DEFAULT(emailAddress);
        } else {
            return SchiriConfiguration.fromJSON(email.get().getContent());
        }
    }

    private Optional<Email> findConfigEmail(String emailAddress) throws MessagingException {
        EmailFolder schiedsrichter = getFolder("SCHIEDSRICHTER");

        for(Email email:schiedsrichter.getEmails()){
            if(email.isFrom(emailAddress)){
                return Optional.of(email);
            }
        }
        return Optional.empty();
    }


    private Optional<Email> findConfigEmail(Schiedsrichter schiedsrichter) throws MessagingException {
        EmailFolder schiedsrichterFolder = getFolder("SCHIEDSRICHTER");
        String emailSubject = schiedsrichter.nachname()+ ", " + schiedsrichter.vorname();

        for(Email email:schiedsrichterFolder.getEmails()){
            if(email.hasSubject(emailSubject)){
                return Optional.of(email);
            }
        }
        return Optional.empty();

    }

    public void overwriteSchiriConfiguration(SchiriConfiguration config) throws MessagingException, JsonProcessingException {

        // first, lets search for an old config
        Optional<Email> oldConfigEmail = findConfigEmail(config.Benutzerdaten.Email);

        // then, update
        saveSchiriConfig(config);

        // last - delete the old one
        if(oldConfigEmail.isPresent()){
            Email oldConfig2 = oldConfigEmail.get();
            oldConfig2.deleteImmediately();
        }
    }

    private void saveSchiriConfig(SchiriConfiguration config) throws MessagingException, JsonProcessingException {
        EmailFolder schiedsrichter = getFolder("SCHIEDSRICHTER");
        SchiriConfigEmail configEmail = new SchiriConfigEmail(config, session);
        schiedsrichter.upload(configEmail.getEmail());
    }

    @Override
    public void close() throws Exception {
        if (store != null && store.isConnected()) {
            store.close();
        }
    }

    public Optional<SchiriConfiguration> findConfigByEmail(String emailAddress) throws MessagingException, IOException {
        Optional<Email> optionalConfigEmail = findConfigEmail(emailAddress);
        if(optionalConfigEmail.isPresent()){
            Email configEmail = optionalConfigEmail.get();
            return Optional.of(SchiriConfiguration.fromJSON(configEmail.getContent()));
        }
        return Optional.empty();
    }

    public Optional<SchiriConfiguration> findConfigByName(Schiedsrichter schiedsrichter) throws MessagingException, IOException {
        Optional<Email> optionalConfigEmail = findConfigEmail(schiedsrichter);
        if(optionalConfigEmail.isPresent()){
            Email configEmail = optionalConfigEmail.get();
            return Optional.of(SchiriConfiguration.fromJSON(configEmail.getContent()));
        }
        return Optional.empty();
    }

}
