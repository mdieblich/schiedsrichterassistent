package com.dieblich.handball.schiedsrichterassistent.mail;

import jakarta.mail.Folder;
import jakarta.mail.MessagingException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.PrintWriter;
import java.io.StringWriter;

@RestController
public class MailController {

    private final EmailServer strato;

    @SuppressWarnings("unused")
    public MailController() {
        strato = new EmailServer(
                "imap.strato.de",
                993,
                "schiribot@fritz.koeln",
                "tnMjQhgRaaTGomq-qBV*tyoA97t7Z!hB");
    }

    @PostMapping("/checkFolderStructure")
    public String checkFolderStructure() throws MessagingException {
        StringBuilder vorher = new StringBuilder();
        for (Folder folder: strato.listFolders()) {
            vorher.append(folder.getFullName()).append("\n");
        }
        strato.getFolder("SCHIEDSRICHTER");
        strato.getFolder("KONFIGURATION");
        StringBuilder nachher = new StringBuilder();
        for (Folder folder: strato.listFolders()) {
            nachher.append(folder.getFullName()).append("\n");
        }
        return "VORHER:\n" + vorher + "\n================\nNachher:\n" + nachher;
    }


//    @GetMapping("/mailtest")
//    public String mailtest() {
//
//        try (Store emailStore = connectStore();
//             Folder inbox = openInbox(emailStore)) {
//            Message[] messages = inbox.getMessages();
//            String result = "Inbox: " + inbox.getMessageCount() + "\n";
//            for (Message message : messages) {
//                result += " " + message.getSubject() + "\n";
//            }
//            return result;
//        } catch (Exception ex) {
//            return returnErrorAsString(ex);
//        }
//
//    }

//    @PostMapping("/createMail")
//    public String createMail() {
//
//        try (Store emailStore = connectStore();
//             Folder inbox = openInbox(emailStore)) {
//            Message[] messages = new Message[1];
//            messages[0] = new MimeMessage(session);
//            messages[0].setSubject("Testnachricht " + inbox.getMessageCount());
//            messages[0].setText("Inhalt der Testnachricht " + inbox.getMessageCount());
//            inbox.appendMessages(messages);
//            return "done";
//        } catch (Exception ex) {
//            return returnErrorAsString(ex);
//        }
//    }

    private String returnErrorAsString(Exception ex) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        return "Error: " + ex.getMessage() + "\nStacktrace:\n" + sw.toString();
    }
}
