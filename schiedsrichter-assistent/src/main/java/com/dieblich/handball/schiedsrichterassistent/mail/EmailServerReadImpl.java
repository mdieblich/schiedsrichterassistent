package com.dieblich.handball.schiedsrichterassistent.mail;

import jakarta.mail.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class EmailServerReadImpl implements AutoCloseable, EmailServerRead {

    private static final Logger logger = LoggerFactory.getLogger(EmailServerReadImpl.class);

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
            logger.info("imap Store established");
        }
        if (!store.isConnected()) {
            store.connect(host, port, user, password);
            logger.info("Connected as {} at {}:{}", this.user, this.host, this.port);
        }

    }

    @Override
    public EmailFolder fetchFolder(String name) throws EmailException {
        try {
            ensureConnection();
            Folder defaultFolder = store.getDefaultFolder();
            Folder folder = defaultFolder.getFolder(name);
            if (!folder.exists()) {
                folder.create(Folder.HOLDS_FOLDERS | Folder.HOLDS_MESSAGES);
                logger.info("Created folder {}" , name);
            }
            folder.open(Folder.READ_WRITE);
            logger.info("Opened folder {}" , name);
            return new EmailFolderImpl(folder, session);
        } catch(Exception e){
            throw new EmailException("Ordner " + name + " konnte nicht geladen werden", e);
        }
    }

    @Override
    public void close() throws Exception {
        if (store != null && store.isConnected()) {
            store.close();
        }
    }

}
