package com.dieblich.handball.schiedsrichterassistent.mail;

import jakarta.mail.Folder;
import jakarta.mail.MessagingException;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@SuppressWarnings("unused")
@RestController
public class MailController {

    private final EmailServer strato;

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
        StringBuilder nachher = new StringBuilder();
        for (Folder folder: strato.listFolders()) {
            nachher.append(folder.getFullName()).append("\n");
        }
        return "VORHER:\n" + vorher + "\n================\nNachher:\n" + nachher;
    }
    @GetMapping("/configuration/{email}")
    public String getConfiguration(@PathVariable(value="email") String email) throws IOException, MessagingException {
        UserConfiguration config = strato.loadUserConfiguration(email);
        return config.toString();
    }

    @PatchMapping("/configuration/{email}")
    public String updateConfiguration(@PathVariable(value="email") String email, @RequestBody Map<String, String> propertiesUpdate) throws MessagingException, IOException {
        UserConfiguration config = strato.loadUserConfiguration(email);
        String vorher = config.toString();

        config.updateWith(propertiesUpdate);
        strato.overwriteUserConfiguration(config);

        String nachher = config.toString();
        return "VORHER:\n" + vorher + "\n================\nNachher:\n" + nachher;
    }

    @PostMapping("/tasks/checkInbox")
    public void checkInbox() throws MessagingException, IOException {
        EmailFolder inbox = strato.getFolder("INBOX");
        Map<String, Email> unknownSenders = new HashMap<>();
        try {
            for (Email email : inbox.getEmails()) {
                Optional<String> optionalSender = email.getFrom();
                if (optionalSender.isPresent()) {
                    String sender = optionalSender.get();
                    if(!unknownSenders.containsKey(sender)){
                        Optional<UserConfiguration> optionalUserConfig = strato.findConfig(sender);
                        if(optionalUserConfig.isPresent()){
                            handleEmail(email);
                        }else{
                            unknownSenders.put(sender, email);
                        }
                    }
                }
            }
            for (Email email : unknownSenders.values()) {
                askForRegistration(email);
            }
        } finally {
            inbox.deleteAll();
        }
    }

    private void askForRegistration(Email email) throws MessagingException, IOException {
        // TODO
        System.out.println("REGISTER: " + email.getFrom() + " - " + email.getContent());
    }

    private void handleEmail(Email email) throws MessagingException, IOException {
        System.out.println("HANDLE: " + email.getFrom() + " - " + email.getContent());
    }


    private String returnErrorAsString(Exception ex) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        return "Error: " + ex.getMessage() + "\nStacktrace:\n" + sw;
    }
}
