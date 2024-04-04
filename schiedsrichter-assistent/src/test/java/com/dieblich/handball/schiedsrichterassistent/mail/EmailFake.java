package com.dieblich.handball.schiedsrichterassistent.mail;

import jakarta.mail.Message;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class EmailFake extends Email{

    private final EmailFolderFake folder;
    @Setter
    String from;
    @Getter
    @Setter
    String subject;
    @Getter
    @Setter
    String content;

    public EmailFake(EmailFolderFake folder, String from, String subject, String content) {
        super((Message) null);
        this.folder = folder;
        this.from = from;
        this.subject = subject;
        this.content = content;
    }

    @Override
    List<String> getAllSenders() {
        return List.of(from);
    }

    @Override
    public void deleteImmediately(){
        folder.delete(this);
    }

}
