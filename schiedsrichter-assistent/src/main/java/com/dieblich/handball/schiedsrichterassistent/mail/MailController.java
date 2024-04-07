package com.dieblich.handball.schiedsrichterassistent.mail;

import com.dieblich.handball.schiedsrichterassistent.*;
import com.dieblich.handball.schiedsrichterassistent.calendar.SpielTerminBeifahrer;
import com.dieblich.handball.schiedsrichterassistent.calendar.SpielTerminEinzelschiri;
import com.dieblich.handball.schiedsrichterassistent.calendar.SpielTerminFahrer;
import com.dieblich.handball.schiedsrichterassistent.geo.GeoException;
import com.dieblich.handball.schiedsrichterassistent.geo.GeoService;
import com.dieblich.handball.schiedsrichterassistent.geo.GeoServiceImpl;
import com.dieblich.handball.schiedsrichterassistent.mail.received.AnsetzungsEmail;
import com.dieblich.handball.schiedsrichterassistent.mail.templates.*;
import jakarta.annotation.PostConstruct;
import jakarta.mail.MessagingException;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;


@SuppressWarnings("unused")
@Configuration
@EnableScheduling
public class MailController {

    private static final Logger logger = LoggerFactory.getLogger(MailController.class);
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

    private EmailServerReadImpl stratoRead;
    private EmailServerSend stratoSend;
    private SchiriRepo schiriRepo;
    private GeoService geoService;

    @PostConstruct
    public void init() {
        stratoRead = new EmailServerReadImpl(imapHost,993,botUsername,botPassword);
        stratoSend = new EmailServerSend(smtpHost,587,botUsername,botPassword);
        schiriRepo = new SchiriRepoEmail(stratoRead);
        geoService = new GeoServiceImpl(openRouteApikey);
    }

    @Scheduled(fixedDelay = 15*1000)
    public void checkInbox() {
        System.out.println("Check the inbox " + LocalDateTime.now());
        EmailFolder inbox = null;
        try{
            inbox = stratoRead.getFolder("INBOX");
            List<Email> allEmails = inbox.getEmails();

            Set<String> unknownSenders = new HashSet<>();
            // TODO First, sort them by sender, i.e. Map<Sender, Email>, then they can be sorted: Config-Update first, others later
            for (Email email : inbox.getEmails()) {

                Response response = handleEmail(email);
                if(response == Response.WelcomeEmail){

                }





                Optional<String> optionalSender = email.getFrom();
                if (optionalSender.isEmpty()) {
                    continue;
                }
                String sender = optionalSender.get();

                if (isConfigUpdate(email)) {
                    handleConfigUpdate(email);
                    // TODO if the config update was successful, all other Emails of that sender should be handled - instead of discarded
                } else {
                    Optional<SchiriConfiguration> optionalSchiriConfig = stratoRead.findConfigByEmail(sender);
                    if (optionalSchiriConfig.isPresent()) {
                        SchiriConfiguration schiriConfiguration = optionalSchiriConfig.get();
                        if (schiriConfiguration.isComplete()) {
                            handleEmailForRegisteredSchiri(email, schiriConfiguration);
                        } else {
                            askForMissingConfig(sender);
                        }
                    } else {
                        unknownSenders.add(sender);
                    }
                }
            }
            for (String unknownSender : unknownSenders) {
                askForRegistration(unknownSender);
            }
        } catch (Exception e) {
            logger.error("Error while checking inbox", e);
        } finally {
            if(inbox!= null){
                try {
                    inbox.deleteAll();
                } catch (MessagingException e) {
                    logger.error("Error while purging inbox", e);
                }
            }
        }
    }
    public InboxDistinction differentiateEmails(List<Email> emails){
        InboxDistinction distinction = new InboxDistinction();
        for(Email email: emails){
            try {
                Optional<String> optionalSender = email.getFrom();
                if (optionalSender.isEmpty()) {
                    continue;
                }
                String sender = optionalSender.get();
                if (isConfigUpdate(email)) {
                    distinction.addConfigUpdate(sender, email);
                } else {
                    if (distinction.isUnknownSender(sender)) {
                        Optional<SchiriConfiguration> optionalConfig = schiriRepo.findConfigByEmail(sender);
                        if (optionalConfig.isPresent()) {
                            distinction.setSenderHasOldConfig(sender);
                        }
                    }
                    distinction.add(sender, email);
                }
            } catch (Exception e) {
                logger.error("Fehler beim Verarbeiten der Email " + email, e);
            }
        }
        return distinction;
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

    private void handleEmailForRegisteredSchiri(Email email, SchiriConfiguration config) {
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

    enum Response{
        WelcomeEmail,
        other
    }

    private Response handleEmail(Email email){
        return Response.other;
    }

    class InboxDistinction{

        Map<String, EmailDistinction> senderToEmails = new HashMap<>();

        void addConfigUpdate(String sender, Email configUpdateEmail){
            EmailDistinction distinction = getEmailDistinction(sender);
            distinction.configEmails.add(configUpdateEmail);
        }

        private EmailDistinction getEmailDistinction(String sender){
            if(!senderToEmails.containsKey(sender)){
                senderToEmails.put(sender, new EmailDistinction());
            }
            return senderToEmails.get(sender);
        }

        public boolean isUnknownSender(String sender) {
            return !getEmailDistinction(sender).isKnown();
        }

        public void setSenderHasOldConfig(String sender) {
            EmailDistinction distinction = getEmailDistinction(sender);
            distinction.hasOldConfig = true;
        }

        public void add(String sender, Email email) {
            getEmailDistinction(sender)
                    .emailsToHandle.add(email);
        }

        class EmailDistinction{
            private boolean hasOldConfig = false;

            public boolean isKnown(){
                return hasOldConfig || !configEmails.isEmpty();
            }
            List<Email> configEmails = new ArrayList<>();
            List<Email> emailsToHandle = new ArrayList<>();
        }
    }

}
