package com.dieblich.handball.schiedsrichterassistent.mail;

import jakarta.mail.internet.MimeMessage;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.mail.*;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Properties;

@RestController
public class MailController {

    @GetMapping("/mailtest")
    public String mailtest() {

        Session session = createSession();
        try (Folder inbox = openInbox(session)) {
            Message[] messages = inbox.getMessages();
            String result = "Inbox: " + inbox.getMessageCount() + "\n";
            for (Message message: messages) {
                result += " " + message.getSubject() + "\n";
            }
            return result;
        } catch (Exception ex) {
            return "Error: " + ex.getMessage();
        }

    }

    @PostMapping("/createMail")
    public String createMail(){

        Session session = createSession();
        try (Folder inbox = openInbox(session)) {
            Message[] messages = new Message[1];
            messages[0] = new MimeMessage(session);
            messages[0].setSubject("Testnachricht " + inbox.getMessageCount());
            messages[0].setText("Inhalt der Testnachricht " + inbox.getMessageCount());
            inbox.appendMessages(messages);
            return "done";
        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            return "Error: " + ex.getMessage() + "\nStacktrace:\n" + sw.toString();
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
