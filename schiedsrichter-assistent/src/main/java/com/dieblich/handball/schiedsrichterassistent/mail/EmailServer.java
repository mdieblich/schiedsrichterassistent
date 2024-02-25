package com.dieblich.handball.schiedsrichterassistent.mail;

import ch.qos.logback.core.net.SyslogOutputStream;
import jakarta.mail.*;

import java.util.Optional;
import java.util.Properties;

public class EmailServer implements AutoCloseable{

    private final Session session;
    private Store store;
    private final String host;
    private final int port;
    private final String user;
    private final String password;

    public EmailServer(String host, int port, String user, String password) throws MessagingException {
        this.host = host;
        this.port = port;
        this.user = user;
        this.password = password;
        Properties props = System.getProperties();
        props.put("mail.imap.ssl.enable", true);
        session = Session.getInstance(props, null);
    }

    private void ensureConnection() throws MessagingException {
        if(store == null){
            store =  session.getStore("imap");
        }
        if(!store.isConnected()){
            store.connect(host, port, user, password);
        }

    }

    public Folder[] listFolders() throws MessagingException {
        ensureConnection();
        return store.getDefaultFolder().list();
    }

    public Folder getInbox() throws MessagingException {
        return getFolder("INBOX");
    }
    public Folder getFolder(String name) throws MessagingException {
        ensureConnection();
        Folder defaultFolder = store.getDefaultFolder();
        Folder folder = defaultFolder.getFolder(name);
        if(!folder.exists()){
            folder.create(Folder.HOLDS_FOLDERS | Folder.HOLDS_MESSAGES);
        }
        folder.open(Folder.READ_ONLY);
        return folder;
    }

    @Override
    public void close() throws Exception {
        if(store.isConnected()){
            store.close();
        }
    }
}
