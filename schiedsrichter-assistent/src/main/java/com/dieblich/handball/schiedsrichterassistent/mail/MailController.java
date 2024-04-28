package com.dieblich.handball.schiedsrichterassistent.mail;

import com.dieblich.handball.schiedsrichterassistent.*;
import com.dieblich.handball.schiedsrichterassistent.calendar.SpielTermin;
import com.dieblich.handball.schiedsrichterassistent.calendar.SpielTerminBeifahrer;
import com.dieblich.handball.schiedsrichterassistent.calendar.SpielTerminEinzelschiri;
import com.dieblich.handball.schiedsrichterassistent.calendar.SpielTerminFahrer;
import com.dieblich.handball.schiedsrichterassistent.geo.GeoException;
import com.dieblich.handball.schiedsrichterassistent.geo.GeoService;
import com.dieblich.handball.schiedsrichterassistent.geo.GeoServiceImpl;
import com.dieblich.handball.schiedsrichterassistent.mail.received.AnsetzungsEmail;
import com.dieblich.handball.schiedsrichterassistent.mail.templates.*;
import com.dieblich.handball.schiedsrichterassistent.pdf.Kostenabrechnung;
import jakarta.annotation.PostConstruct;
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
    private String botEmailaddress;
    @Value("${mail.password}")
    private String botPassword;

    private SchiriRepo schiriRepo;
    private Inbox inbox;
    private EmailServerSend stratoSend;
    private GeoService geoService;

    @PostConstruct
    public void init() {
        EmailServerReadImpl stratoRead = new EmailServerReadImpl(imapHost, 993, botEmailaddress, botPassword);
        schiriRepo = new SchiriRepoEmail(stratoRead);
        inbox = new Inbox(stratoRead);

        stratoSend = new EmailServerSend(smtpHost,587, botEmailaddress,botPassword);
        geoService = new GeoServiceImpl(openRouteApikey);
    }

    @Scheduled(fixedDelay = 15*1000)
    public void checkInbox() {
        System.out.println("Check the inbox " + LocalDateTime.now());
        try{
            inbox.checkEmails();
        } catch (EmailException e) {
            logger.error("Fehler beim Laden der Emails aus der Inbox", e);
        }
        for(Email configEmail: inbox.getConfigEmails()){
            handleConfigUpdate(configEmail);
        }

        Map<String, SchiriConfiguration> fullyConfiguredSchiris = new HashMap<>();
        List<String> partlyConfiguredSchiris = new ArrayList<>();
        for(String knownSchiri:inbox.getKnownSchiris()){
            try {
                Optional<SchiriConfiguration> optionalSchiriConfig = schiriRepo.findConfigByEmail(knownSchiri);
                if (optionalSchiriConfig.isPresent() && optionalSchiriConfig.get().isComplete()) {
                    fullyConfiguredSchiris.put(knownSchiri, optionalSchiriConfig.get());
                } else {
                    partlyConfiguredSchiris.add(knownSchiri);
                }
            } catch (SchiriRepo.SchiriRepoException e) {
                partlyConfiguredSchiris.add(knownSchiri);
                inbox.addException(knownSchiri, e);
            }
        }

        for(Map.Entry<String, SchiriConfiguration> entry: fullyConfiguredSchiris.entrySet()){
            for(Email email:inbox.getAllOtherEmailsForSchiri(entry.getKey())){
                handleEmailForRegisteredSchiri(email, entry.getValue());
            }
        }

        for(String schiri:partlyConfiguredSchiris){
            askForMissingConfig(schiri);
        }

        for(String unknownSchiri: inbox.getUnknownSchiris()){
            askForRegistration(unknownSchiri);
        }
        for(Map.Entry<String, List<Exception>> exceptions: inbox.getExceptions().entrySet()){
            sendErrorEmail(exceptions.getKey(), exceptions.getValue());
        }

        try {
            inbox.purge();
        } catch (EmailException e) {
            logger.error("Fehler beim Löschen der Emails im Posteingang", e);
        }
    }

    private void handleConfigUpdate(Email email) {
        String sender = email.getSender();
        try{
            Optional<SchiriConfiguration> optionalOldConfig = schiriRepo.findConfigByEmail(sender);

            SchiriConfiguration config = optionalOldConfig.orElse(SchiriConfiguration.NEW_DEFAULT(sender));

            config.updateWith(email.getContent(), geoService::findKoordinaten);
            schiriRepo.overwriteSchiriConfiguration(config);

            ConfigConfirmationEmail responseEmail = new ConfigConfirmationEmail(botEmailaddress, sender, config);
            stratoSend.send(responseEmail);
        } catch (Exception e) {
            inbox.addException(sender, e);
        }
    }

    private void askForMissingConfig(String sender){
        AskForConfigurationEmail email = new AskForConfigurationEmail(botEmailaddress, sender);
        try {
            stratoSend.send(email);
        } catch (EmailException e) {
            logger.error("Konnte keine AskForConfigurationEmail senden an {}", sender, e);
        }
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
                DontKnowWhatToDoEmail response = new DontKnowWhatToDoEmail(botEmailaddress, config.Benutzerdaten.Email, email);
                stratoSend.send(response);
            }
        } catch(Exception e){
            inbox.addException(config.Benutzerdaten.Email, e);
        }
    }

    private boolean isAnsetzung(Email email) {
        return email.getSubject().contains("Spielansetzung");
    }

    private void askForRegistration(String newUserEmail){
        WelcomeEmail welcomeEmail = new WelcomeEmail(botEmailaddress, newUserEmail);
        try {
            stratoSend.send(welcomeEmail);
        } catch (EmailException e) {
            logger.error("Konnte keine WelcomeEmail senden an {}", newUserEmail, e);
        }
    }

    @Nullable
    private SchiriConfiguration findGespannpartner(SchiriConfiguration config, SchiriEinsatz schiriEinsatz) throws SchiriRepo.SchiriRepoException, EmailException {
        String emailSchiriA = config.Benutzerdaten.Email;
        Schiedsrichter otherSchiri = schiriEinsatz.otherSchiri(config.Benutzerdaten.getSchiedsrichter());

        Optional<SchiriConfiguration> optionalSchiriBConfig = schiriRepo.findConfigByName(otherSchiri);
        // TODO We could find multiple Schiris with the same name. Better check the Email as well
        if (optionalSchiriBConfig.isEmpty()) {
            SecondSchiriMissingEmail schiriMissingEmail = new SecondSchiriMissingEmail(botEmailaddress, emailSchiriA, otherSchiri);
            stratoSend.send(schiriMissingEmail);
            return null;
        }
        SchiriConfiguration schiriConfigB = optionalSchiriBConfig.get();
        if (!schiriConfigB.hasGespannpartner(config)) {
            YouAreNotWhitelistedEmail notInWhitelist = new YouAreNotWhitelistedEmail(botEmailaddress, emailSchiriA, otherSchiri);
            stratoSend.send(notInWhitelist);
            ExtendWhitelistEmail extendWhitelist = new ExtendWhitelistEmail(botEmailaddress, schiriConfigB, config.Benutzerdaten);
            stratoSend.send(extendWhitelist);
        }
        return schiriConfigB;
    }

    private void sendCalendarEventForOneSchiedsrichter(SchiriEinsatz schiriEinsatz, SchiriConfiguration config) throws GeoException, ConfigException, IOException, EmailException {
        SpielTerminEinzelschiri spielTermin = new SpielTerminEinzelschiri(schiriEinsatz, config, geoService);
        Kostenabrechnung abrechnung = new Kostenabrechnung(schiriEinsatz, spielTermin.getSpielAblauf(), config);
        sendTermin(spielTermin, abrechnung, config.Benutzerdaten.Email);
    }
    private void sendCalendarEventForTwoSchiedsrichter(SchiriEinsatz schiriEinsatz, SchiriConfiguration fahrer, SchiriConfiguration beifahrer) throws GeoException, ConfigException, IOException, EmailException {
        SpielTerminFahrer spielTerminFahrer = new SpielTerminFahrer(schiriEinsatz, fahrer, beifahrer, geoService);
        Kostenabrechnung abrechnung = new Kostenabrechnung(schiriEinsatz, spielTerminFahrer.getSpielAblauf(), fahrer, beifahrer);
        sendTermin(spielTerminFahrer, abrechnung, fahrer.Benutzerdaten.Email);
        SpielTerminBeifahrer spielTerminBeifahrer = spielTerminFahrer.createBeifahrerTermin();
        sendTermin(spielTerminBeifahrer, abrechnung, beifahrer.Benutzerdaten.Email);
    }

    private void sendTermin(SpielTermin termin, Kostenabrechnung abrechnung, String receiver) throws GeoException, ConfigException, IOException, EmailException {
        try (CalendarResponseEmail response = new CalendarResponseEmail(botEmailaddress, receiver, termin, abrechnung)) {
            stratoSend.send(response);
        }
    }

    private void sendErrorEmail(String receiver, List<Exception> exceptions){
        ErrorEmail email = new ErrorEmail(botEmailaddress, receiver, exceptions);
        try {
            stratoSend.send(email);
        } catch (EmailException e) {
            logger.error("Konnte Fehleremail nicht senden, ", e);
            for(int i=0; i<exceptions.size(); i++){
                logger.error("Fehlermeldung {}", i, exceptions.get(i));
            }
        }
    }

}
