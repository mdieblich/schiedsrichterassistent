package com.dieblich.handball.schiedsrichterassistent.mail;

import com.dieblich.handball.schiedsrichterassistent.config.SchiriConfiguration;
import com.dieblich.handball.schiedsrichterassistent.config.SchiriRepo;
import com.dieblich.handball.schiedsrichterassistent.config.SchiriRepoEmail;
import com.dieblich.handball.schiedsrichterassistent.mail.templates.WelcomeEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class Inbox{

    private static final Logger logger = LoggerFactory.getLogger(Inbox.class);

    private final EmailServerRead emailServer;
    private final SchiriRepo schiriRepo;
    private EmailFolder inboxFolder;

    private final Map<String, SchiriInbox> schirisInboxes = new HashMap<>();

    static class SchiriInbox {
        private boolean hasOldConfig = false;
        final List<Email> configEmails = new ArrayList<>();
        final List<Email> emailsToHandle = new ArrayList<>();
        final List<Exception> exceptions = new ArrayList<>();

        public boolean schiriIsUnknown(){
            return !hasOldConfig && configEmails.isEmpty();
        }

        private String summary(){
            return
                configEmails.size() + " config updates, " +
                emailsToHandle.size() + " normal emails, " +
                exceptions.size() + " exceptions.";
        }
    }

    public Inbox(EmailServerRead emailServer){
        this.emailServer = emailServer;
        schiriRepo = new SchiriRepoEmail(emailServer);
    }

    public void checkEmails() throws EmailException {
        logger.info("Checking Emails.");
        inboxFolder = emailServer.fetchFolder("INBOX");
        logger.info("Categorizing {} Emails:", inboxFolder.getEmails().size());
        for(Email email: inboxFolder.getEmails()){
            categorizeEmail(email);
        }
        logger.info("Emails from {} differnt Schiris categorized:", schirisInboxes.size());
        for(Map.Entry<String, SchiriInbox> inbox: schirisInboxes.entrySet()){
            logger.info("{}: {}", inbox.getKey(), inbox.getValue().summary());
        }
    }

    private void categorizeEmail(Email email){
        logger.info("Categorizing <{}> \"{}\".", email.getSender(), email.getSubject());
        SchiriInbox schiriInbox = getSchiriInbox(email.getSender());
        if (isConfigUpdate(email)) {
            logger.info("Category: Config update.");
            schiriInbox.configEmails.add(email);
        } else {
            logger.info("Category: Normal email.");
            if (schiriInbox.schiriIsUnknown()) {
                logger.debug("Unsure if the author is known. Searching for old config...");
                try {
                    Optional<SchiriConfiguration> optionalConfig = schiriRepo.findConfigByEmail(email.getSender());
                    if (optionalConfig.isPresent()) {
                        logger.debug("...old config found");
                        schiriInbox.hasOldConfig = true;
                    }
                } catch (SchiriRepo.SchiriRepoException e) {
                    schiriInbox.exceptions.add(e);
                }
            }
            schiriInbox.emailsToHandle.add(email);
        }
    }
    public void addException(String schiri, Exception e) {
        getSchiriInbox(schiri).exceptions.add(e);
    }

    private SchiriInbox getSchiriInbox(String sender){
        if(!schirisInboxes.containsKey(sender)){
            schirisInboxes.put(sender, new SchiriInbox());
        }
        return schirisInboxes.get(sender);
    }

    private boolean isConfigUpdate(Email email) {
        boolean isAReplyToAWelcomeEmail = email.getSubject().contains(WelcomeEmail.SUBJECT);
        boolean isRegularConfigUpdate = email.getSubject().contains("Konfiguration");
        return isAReplyToAWelcomeEmail || isRegularConfigUpdate;
    }

    public List<Email> getConfigEmails(){
        List<Email> configEmails = new ArrayList<>();
        for(SchiriInbox schiriInbox : schirisInboxes.values()){
            configEmails.addAll(schiriInbox.configEmails);
        }
        return configEmails;
    }

    public Set<String> getKnownSchiris(){
        return schirisInboxes.entrySet().stream()
                .filter(entry -> !entry.getValue().schiriIsUnknown())
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

    }

    public List<Email> getAllOtherEmailsForSchiri(String schiri) {
        return schirisInboxes.get(schiri).emailsToHandle;
    }

    public Set<String> getUnknownSchiris(){
        return schirisInboxes.entrySet().stream()
                .filter(entry -> entry.getValue().schiriIsUnknown())
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

    }
    public Map<String, List<Exception>> getExceptions() {
        Map<String, List<Exception>> occurredExceptions = new HashMap<>();
        for(Map.Entry<String, SchiriInbox> inbox:schirisInboxes.entrySet()){
            String schiriEmail = inbox.getKey();
            List<Exception> exceptions = inbox.getValue().exceptions;
            if(!exceptions.isEmpty()){
                occurredExceptions.put(schiriEmail, exceptions);
            }
        }
        return occurredExceptions;
    }
    public void purge() throws EmailException {
        logger.info("Purging emails");
        if(inboxFolder != null){
            schirisInboxes.clear();
            inboxFolder.deleteAll();
            inboxFolder = null;
        }
    }

}
