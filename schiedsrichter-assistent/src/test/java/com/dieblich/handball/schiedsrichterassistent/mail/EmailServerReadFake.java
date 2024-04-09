package com.dieblich.handball.schiedsrichterassistent.mail;

import java.util.HashMap;
import java.util.Map;

public class EmailServerReadFake implements EmailServerRead{

    private final Map<String, EmailFolderFake> folders = new HashMap<>();

    public EmailFolderFake createFolder(String name){
        EmailFolderFake newFolder = new EmailFolderFake();
        folders.put(name, newFolder);
        return newFolder;
    }

    @Override
    public EmailFolder fetchFolder(String name) {
        return folders.get(name);
    }
}
