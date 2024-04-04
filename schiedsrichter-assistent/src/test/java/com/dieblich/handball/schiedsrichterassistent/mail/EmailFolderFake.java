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

    @Override
    public Email prepareEmailForUpload() {
        return new EmailFake(this, null, null, null);
    }

    public void createEmail(String from, String subject, String content) {
        upload(new EmailFake(this, from, subject, content));
    }

    public void delete(EmailFake email) {
        emails.remove(email);
    }
}
