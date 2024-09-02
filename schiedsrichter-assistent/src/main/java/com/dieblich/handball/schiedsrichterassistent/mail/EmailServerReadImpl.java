package com.dieblich.handball.schiedsrichterassistent.mail;

import jakarta.mail.*;

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
    public EmailFolder fetchFolder(String name) throws EmailException {
        try {
            ensureConnection();
            Folder defaultFolder = store.getDefaultFolder();
            Folder folder = defaultFolder.getFolder(name);
            if (!folder.exists()) {
                folder.create(Folder.HOLDS_FOLDERS | Folder.HOLDS_MESSAGES);
            }
            folder.open(Folder.READ_WRITE);
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
