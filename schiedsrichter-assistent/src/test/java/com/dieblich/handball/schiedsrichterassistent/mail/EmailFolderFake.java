package com.dieblich.handball.schiedsrichterassistent.mail;

import java.util.ArrayList;
import java.util.List;

public class EmailFolderFake implements EmailFolder{

    private final List<Email> emails = new ArrayList<>();

    @Override
    public List<Email> getEmails(){
        return emails;
    }

    @Override
    public void upload(Email email){
        emails.add(email);
    }

    @Override
    public void deleteAll(){
        emails.clear();
    }

    public EmailFake createEmail(String from, String subject, String content) {
        EmailFake newMail = new EmailFake(this, from, "", subject, content);
        upload(newMail);
        return newMail;
    }

    public void delete(Email email) {
        emails.remove(email);
    }
}
