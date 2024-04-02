package com.dieblich.handball.schiedsrichterassistent.mail;

import com.dieblich.handball.schiedsrichterassistent.MissingConfigException;
import com.dieblich.handball.schiedsrichterassistent.Schiedsrichter;
import com.dieblich.handball.schiedsrichterassistent.SchiriConfiguration;
import com.dieblich.handball.schiedsrichterassistent.SchiriEinsatz;
import com.dieblich.handball.schiedsrichterassistent.calendar.SpielTerminBeifahrer;
import com.dieblich.handball.schiedsrichterassistent.calendar.SpielTerminEinzelschiri;
import com.dieblich.handball.schiedsrichterassistent.calendar.SpielTerminFahrer;
import com.dieblich.handball.schiedsrichterassistent.geo.GeoException;
import com.dieblich.handball.schiedsrichterassistent.geo.GeoService;
import com.dieblich.handball.schiedsrichterassistent.geo.GeoServiceImpl;
import com.dieblich.handball.schiedsrichterassistent.mail.received.AnsetzungsEmail;
import com.dieblich.handball.schiedsrichterassistent.mail.templates.*;
import jakarta.annotation.PostConstruct;
import jakarta.mail.Folder;
import jakarta.mail.MessagingException;
import org.jetbrains.annotations.Nullable;
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
    private GeoService geoService;

    @PostConstruct
    public void init() {
        stratoRead = new EmailServerRead(imapHost,993,botUsername,botPassword);
        stratoSend = new EmailServerSend(smtpHost,587,botUsername,botPassword);
        geoService = new GeoServiceImpl(openRouteApikey);
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
                        Optional<SchiriConfiguration> optionalSchiriConfig = stratoRead.findConfigByEmail(sender);
                        if(optionalSchiriConfig.isPresent()){
                            SchiriConfiguration schiriConfiguration = optionalSchiriConfig.get();
                            if(schiriConfiguration.isComplete()){
                                handleEmail(email, schiriConfiguration);
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
            inbox.deleteAll();
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
        config.updateWith(email.getContent(), geoService::findKoordinaten, log::add);
        stratoRead.overwriteSchiriConfiguration(config);
        ConfigConfirmationEmail responseEmail = stratoSend.createConfigConfirmationEmail(sender, config, log);
        responseEmail.send();
    }

    private void askForRegistration(String newUserEmail) throws MessagingException{
        WelcomeEmail welcomeEmail = stratoSend.createWelcomeEmail(newUserEmail);
        welcomeEmail.send();
    }

    private void handleEmail(Email email, SchiriConfiguration config) {
        try{
            if(isAnsetzung(email)){
                AnsetzungsEmail ansetzungsEmail = new AnsetzungsEmail(email);
                SchiriEinsatz schiriEinsatz = ansetzungsEmail.extractSchiriEinsatz();
                if(schiriEinsatz.mitGespannspartner()){
                    SchiriConfiguration schiriConfigB = findGespannpartner(config, schiriEinsatz);
                    if (schiriConfigB != null){
                        sendCalendarEventForTwoSchiedsrichter(schiriEinsatz, config, schiriConfigB);
                    }
                } else {
                    sendCalendarEventForOneSchiedsrichter(schiriEinsatz, config);
                }
            } else {
                DontKnowWhatToDoEmail response = stratoSend.createResponseForUnknownEmail(config.Benutzerdaten.Email, email);
                response.send();
            }
        } catch(Exception e){
            e.printStackTrace(System.out);
        }
    }

    @Nullable
    private SchiriConfiguration findGespannpartner(SchiriConfiguration config, SchiriEinsatz schiriEinsatz) throws MessagingException, IOException {
        String emailSchiriA = config.Benutzerdaten.Email;
        Schiedsrichter otherSchiri = schiriEinsatz.otherSchiri(config.Benutzerdaten.getSchiedsrichter());

        Optional<SchiriConfiguration> optionalSchiriBConfig = stratoRead.findConfigByName(otherSchiri);
        // TODO We could find multiple Schiris with the same name. Better check the Email as well
        if(optionalSchiriBConfig.isEmpty()){
            SecondSchiriMissingEmail schiriMissingEmail = stratoSend.createSecondSchiriMissingEmail(emailSchiriA, otherSchiri);
            schiriMissingEmail.send();
            return null;
        }
        SchiriConfiguration schiriConfigB = optionalSchiriBConfig.get();
        if(!schiriConfigB.hasGespannpartner(config)){
            YouAreNotWhitelistedEmail notInWhitelist = stratoSend.createYouAreNotWhitelistedEmail(emailSchiriA, otherSchiri);
            notInWhitelist.send();
            ExtendWhitelistEmail extendWhitelist = stratoSend.createExtendWhitelistEmail(schiriConfigB, config.Benutzerdaten);
            extendWhitelist.send();
        }
        return schiriConfigB;
    }


    private void sendCalendarEventForOneSchiedsrichter(SchiriEinsatz schiriEinsatz, SchiriConfiguration config) throws MessagingException, GeoException, MissingConfigException, IOException {
        SpielTerminEinzelschiri spielTermin = new SpielTerminEinzelschiri(schiriEinsatz, config, geoService);
        try (CalendarResponseEmail response = stratoSend.createCalendarResponse(config.Benutzerdaten.Email, spielTermin)) {
            response.send();
        }
    }
    private void sendCalendarEventForTwoSchiedsrichter(SchiriEinsatz schiriEinsatz, SchiriConfiguration fahrer, SchiriConfiguration beifahrer) throws MessagingException, GeoException, MissingConfigException, IOException {
        SpielTerminFahrer spielTerminFahrer = new SpielTerminFahrer(schiriEinsatz, fahrer, beifahrer, geoService);
        try (CalendarResponseEmail response = stratoSend.createCalendarResponse(fahrer.Benutzerdaten.Email, spielTerminFahrer)) {
            response.send();
        }
        SpielTerminBeifahrer spielTerminBeifahrer = spielTerminFahrer.createBeifahrerTermin();
        try (CalendarResponseEmail response = stratoSend.createCalendarResponse(beifahrer.Benutzerdaten.Email, spielTerminBeifahrer)) {
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
