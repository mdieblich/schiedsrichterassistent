package com.dieblich.handball.schiedsrichterassistent.mail;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.mail.*;
import java.util.Properties;

@RestController
public class MailController {

    @GetMapping("/mailtest")
    public String mailtest() {

        Session session = createSession();
        try (Folder inbox = openInbox(session)) {
            Message[] messages = inbox.getMessages();

            return "Inbox: " + inbox.getMessageCount();
        } catch (Exception ex) {
            return "Error: " + ex.getMessage();
        }

    }

    private Session createSession(){
        Properties props = System.getProperties();
        props.put("mail.imap.ssl.enable", true);
        Session session = Session.getInstance(props, null);
        //session.setDebug(true);

        return session;
    }

    private Folder openInbox(Session session) throws MessagingException {
        Store store = session.getStore("imap");
        store.connect("imap.strato.de", 993, "schiribot@fritz.koeln", "tnMjQhgRaaTGomq-qBV*tyoA97t7Z!hB");
        Folder defaultFolder = store.getDefaultFolder();
        Folder inbox = defaultFolder.getFolder("INBOX");
        inbox.open(Folder.READ_ONLY);
        return inbox;
    }

}
