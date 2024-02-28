package com.dieblich.handball.schiedsrichterassistent.mail;

import jakarta.mail.*;

import java.io.IOException;
import java.util.Optional;
import java.util.Properties;

public class EmailServer implements AutoCloseable {

    private final Session session;
    private Store store;
    private final String host;
    private final int port;
    private final String user;
    private final String password;

    public EmailServer(String host, int port, String user, String password) {
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

    public Folder[] listFolders() throws MessagingException {
        ensureConnection();
        return store.getDefaultFolder().list();
    }

    public EmailFolder getFolder(String name) throws MessagingException {
        ensureConnection();
        Folder defaultFolder = store.getDefaultFolder();
        Folder folder = defaultFolder.getFolder(name);
        if (!folder.exists()) {
            folder.create(Folder.HOLDS_FOLDERS | Folder.HOLDS_MESSAGES);
        }
        folder.open(Folder.READ_WRITE);
        return new EmailFolder(folder);
    }

    public UserConfiguration loadUserConfiguration(String emailAddress) throws IOException, MessagingException {
        Optional<Email> email = findConfigEmail(emailAddress);
        if(email.isEmpty()){
            return UserConfiguration.DEFAULT(emailAddress);
        } else {
            return new UserConfiguration(emailAddress, email.get().getContent());
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

    public void overwriteUserConfiguration(UserConfiguration userConfig) throws MessagingException {

        // first, lets search for an old config
        Optional<Email> oldConfig = findConfigEmail(userConfig.getEmail());

        // then, update
        saveUserConfig(userConfig);

        // last - delete the old one
        if(oldConfig.isPresent()){
            Email oldConfig2 = oldConfig.get();
            oldConfig2.deleteImmediately();
        }
    }

    private void saveUserConfig(UserConfiguration userConfig) throws MessagingException {
        EmailFolder schiedsrichter = getFolder("SCHIEDSRICHTER");
        Email configAsEmail = userConfig.toEmail(session);
        schiedsrichter.upload(configAsEmail);
    }

    @Override
    public void close() throws Exception {
        if (store != null && store.isConnected()) {
            store.close();
        }
    }

    public Optional<UserConfiguration> findConfig(String emailAddress) throws MessagingException, IOException {
        Optional<Email> optionalConfigEmail = findConfigEmail(emailAddress);
        if(optionalConfigEmail.isPresent()){
            Email configEmail = optionalConfigEmail.get();
            return Optional.of(new UserConfiguration(emailAddress, configEmail.getContent()));
        }
        return Optional.empty();
    }
}
