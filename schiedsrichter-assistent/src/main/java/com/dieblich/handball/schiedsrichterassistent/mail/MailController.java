package com.dieblich.handball.schiedsrichterassistent.mail;

import com.dieblich.handball.schiedsrichterassistent.SchiriConfiguration;
import com.dieblich.handball.schiedsrichterassistent.SchiriEinsatz;
import com.dieblich.handball.schiedsrichterassistent.geo.DistanceService;
import com.dieblich.handball.schiedsrichterassistent.mail.received.AnsetzungsEmail;
import com.dieblich.handball.schiedsrichterassistent.mail.templates.AskForConfigurationEmail;
import com.dieblich.handball.schiedsrichterassistent.mail.templates.ConfigConfirmationEmail;
import com.dieblich.handball.schiedsrichterassistent.mail.templates.DontKnowWhatToDoEmail;
import com.dieblich.handball.schiedsrichterassistent.mail.templates.WelcomeEmail;
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
    public SchiriConfiguration getConfiguration(@PathVariable(value="email") String email) throws IOException, MessagingException {
        return stratoRead.loadSchiriConfiguration(email);
    }

    @PatchMapping("/configuration/{email}")
    public String updateConfiguration(@PathVariable(value="email") String emailAddress, @RequestBody SchiriConfiguration configUpdate) throws MessagingException, IOException {
        SchiriConfiguration config = stratoRead.loadSchiriConfiguration(emailAddress);
        String vorher = config.toJSON();

        List<String> log = new ArrayList<>();

        config.updateWith(configUpdate, distanceService::addressToPoint, log::add);
        stratoRead.overwriteSchiriConfiguration(config);

        String nachher = config.toJSON();
        return "VORHER:\n" + vorher + "\n" +
                "================\n" +
                "Nachher:\n" + nachher + "\n" +
                "================\n" +
                "Log:\n" + String.join("\n", log);
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
                        Optional<SchiriConfiguration> optionalSchiriConfig = stratoRead.findConfig(sender);
                        if(optionalSchiriConfig.isPresent()){
                            SchiriConfiguration schiriConfiguration = optionalSchiriConfig.get();
                            if(schiriConfiguration.isComplete()){
                                handleEmail(sender, email, schiriConfiguration);
                            } else{
                                askForMissingConfig(sender);
                            }
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
//            inbox.deleteAll();
        }
    }

    private void askForMissingConfig(String sender) throws MessagingException {
        AskForConfigurationEmail email = stratoSend.createAskForConfigEmail(sender);
        email.send();
    }

    private boolean isConfigUpdate(Email email) throws MessagingException {
        boolean isAReplyToAWelcomeEmail = email.getSubject().contains(WelcomeEmail.SUBJECT);
        boolean isRegularConfigUpdate = email.getSubject().contains("Konfiguration");
        return isAReplyToAWelcomeEmail || isRegularConfigUpdate;
    }

    private void handleConfigUpdate(Email email) throws MessagingException, IOException {
        if(email.getFrom().isEmpty()){ return; }

        String sender = email.getFrom().get();
        SchiriConfiguration config = stratoRead.loadSchiriConfiguration(sender);

        List<String> log = new ArrayList<>();
        config.updateWith(email.getContent(), distanceService::addressToPoint, log::add);
        stratoRead.overwriteSchiriConfiguration(config);
        ConfigConfirmationEmail responseEmail = stratoSend.createConfigConfirmationEmail(sender, config, log);
        responseEmail.send();
    }

    private void askForRegistration(String newUserEmail) throws MessagingException{
        WelcomeEmail welcomeEmail = stratoSend.createWelcomeEmail(newUserEmail);
        welcomeEmail.send();
    }

    private void handleEmail(String sender, Email email, SchiriConfiguration config) throws MessagingException, IOException {
        if(isAnsetzung(email)){
            AnsetzungsEmail ansetzungsEmail = new AnsetzungsEmail(email);
            SchiriEinsatz schiriEinsatz = ansetzungsEmail.extractSchiriEinsatz();
            // TODO Termin daraus erstellen und zuschicken
            System.out.println(schiriEinsatz);

        } else {
            DontKnowWhatToDoEmail response = stratoSend.createResponseForUnknownEmail(sender, email);
            response.send();
        }
    }

    private boolean isAnsetzung(Email email) throws MessagingException {
        return email.getSubject().contains("Spielansetzung");
    }

    private String returnErrorAsString(Exception ex) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        return "Error: " + ex.getMessage() + "\nStacktrace:\n" + sw;
    }
}
