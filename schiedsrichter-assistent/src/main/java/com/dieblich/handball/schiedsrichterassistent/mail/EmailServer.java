package com.dieblich.handball.schiedsrichterassistent.mail;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;

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

    public Folder getFolder(String name) throws MessagingException {
        ensureConnection();
        Folder defaultFolder = store.getDefaultFolder();
        Folder folder = defaultFolder.getFolder(name);
        if (!folder.exists()) {
            folder.create(Folder.HOLDS_FOLDERS | Folder.HOLDS_MESSAGES);
        }
        folder.open(Folder.READ_WRITE);
        return folder;
    }

    public UserConfiguration loadUserConfiguration(String email) throws IOException, MessagingException {
        Optional<Message> message = findConfig(email);
        if(message.isEmpty()){
            return UserConfiguration.DEFAULT(email);
        } else {
            return new UserConfiguration(email, message.get().getContent().toString());
        }
    }

    private Optional<Message> findConfig(String email) throws MessagingException {
        Folder schiedsrichter = getFolder("SCHIEDSRICHTER");
        for(Message message:schiedsrichter.getMessages()){
            for (Address from: message.getFrom()) {
                if(from instanceof InternetAddress){
                    String fromString = ((InternetAddress)from).getAddress();
                    if(email.equals(fromString)){
                        return Optional.of(message);
                    }
                }
            }
        }
        return Optional.empty();
    }

    public void overwriteUserConfiguration(UserConfiguration userConfig) throws MessagingException {

        // first, lets search for an old config
        Optional<Message> oldConfig = findConfig(userConfig.getEmail());

        // then, update
        saveUserConfig(userConfig);

        // last - delete the old one
        if(oldConfig.isPresent()){
            Message oldConfig2 = oldConfig.get();
            oldConfig2.setFlag(Flags.Flag.DELETED, true);
            oldConfig2.getFolder().expunge();
        }
    }

    private void saveUserConfig(UserConfiguration userConfig) throws MessagingException {
        Folder schiedsrichter = getFolder("SCHIEDSRICHTER");
        Message configAsMessage = userConfig.toMessage(session);
        schiedsrichter.appendMessages(new Message[]{configAsMessage});
    }

    @Override
    public void close() throws Exception {
        if (store != null && store.isConnected()) {
            store.close();
        }
    }

}
