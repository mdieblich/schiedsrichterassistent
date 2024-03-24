package com.dieblich.handball.schiedsrichterassistent.mail;

import com.dieblich.handball.schiedsrichterassistent.geo.DistanceService;
import jakarta.annotation.PostConstruct;
import jakarta.mail.Folder;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;

@SuppressWarnings("unused")
@RestController
public class MailController {
    @Value("${openrouteservice.apikey}")
    private String openRouteApikey;

    @Value("${mail.imap.host}")
    private String imapHost;
    @Value("${mail.smtp.host}")
    private String smtpHost;
    @Value("${mail.user}")
    private String botUsername;
    @Value("${mail.password}")
    private String botPassword;

    private EmailServerRead stratoRead;
    private EmailServerSend stratoSend;
    private DistanceService distanceService;

    @PostConstruct
    public void init() {
        stratoRead = new EmailServerRead(imapHost,993,botUsername,botPassword);
        stratoSend = new EmailServerSend(smtpHost,587,botUsername,botPassword);
        distanceService = new DistanceService(openRouteApikey);
    }

    @PostMapping("/checkFolderStructure")
    public String checkFolderStructure() throws MessagingException {
        StringBuilder vorher = new StringBuilder();
        for (Folder folder: stratoRead.listFolders()) {
            vorher.append(folder.getFullName()).append("\n");
        }
        stratoRead.getFolder("SCHIEDSRICHTER");
        StringBuilder nachher = new StringBuilder();
        for (Folder folder: stratoRead.listFolders()) {
            nachher.append(folder.getFullName()).append("\n");
        }
        return "VORHER:\n" + vorher + "\n================\nNachher:\n" + nachher;
    }
    @GetMapping("/configuration/{email}")
    public String getConfiguration(@PathVariable(value="email") String email) throws IOException, MessagingException {
        UserConfiguration config = stratoRead.loadUserConfiguration(email);
        return config.toString();
    }

    @PatchMapping("/configuration/{email}")
    public String updateConfiguration(@PathVariable(value="email") String emailAddress, @RequestBody Map<String, String> propertiesUpdate) throws MessagingException, IOException {
        UserConfiguration config = stratoRead.loadUserConfiguration(emailAddress);
        String vorher = config.toString();

        config.updateWith(propertiesUpdate);
        stratoRead.overwriteUserConfiguration(config);

        String nachher = config.toString();
        return "VORHER:\n" + vorher + "\n================\nNachher:\n" + nachher;
    }

    @PostMapping("/tasks/checkInbox")
    public void checkInbox() throws MessagingException, IOException {
        EmailFolder inbox = stratoRead.getFolder("INBOX");
        Set<String> unknownSenders = new HashSet<>();
        try {
            // TODO First, sort them by sender, i.e. Map<Sender, Email>, then they can be sorted: Config-Update first, others later
            for (Email email : inbox.getEmails()) {
                Optional<String> optionalSender = email.getFrom();
                if (optionalSender.isPresent()) {
                    String sender = optionalSender.get();
                    if(isConfigUpdate(email)){
                        handleConfigUpdate(email);
                        // TODO if the config update was successful, all other Emails of that sender should be handled - instead of discarded
                    } else if(!unknownSenders.contains(sender)){
                        Optional<UserConfiguration> optionalUserConfig = stratoRead.findConfig(sender);
                        if(optionalUserConfig.isPresent()){
                            handleEmail(email);
                        }else{
                            unknownSenders.add(sender);
                        }
                    }
                }
            }
            for (String unknownSender : unknownSenders) {
                askForRegistration(unknownSender);
            }
        } finally {
            inbox.deleteAll();
        }
    }

    private boolean isConfigUpdate(Email email) throws MessagingException {
        boolean isAReplyToAWelcomeEmail = email.getSubject().contains(WelcomeEmail.SUBJECT);
        boolean isRegularConfigUpdate = email.getSubject().contains("Konfiguration");
        return isAReplyToAWelcomeEmail || isRegularConfigUpdate;
    }

    private void handleConfigUpdate(Email email) throws MessagingException, IOException {
        if(email.getFrom().isEmpty()){ return; }

        String sender = email.getFrom().get();
        UserConfiguration oldConfig = stratoRead.loadUserConfiguration(sender);

        // TODO hier weitermachen: Address-Update in GeoLocation umwandeln
        oldConfig.updateWith(email.getContent(), distanceService::addressToGeoLocation);
        stratoRead.overwriteUserConfiguration(oldConfig);
    }

    private void askForRegistration(String newUserEmail) throws MessagingException{
        WelcomeEmail welcomeEmail = stratoSend.createWelcomeEmail(newUserEmail);
        welcomeEmail.send();
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
