package com.dieblich.handball.schiedsrichterassistent.config;

import com.dieblich.handball.schiedsrichterassistent.Schiedsrichter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public interface SchiriRepo {

    Logger logger = LoggerFactory.getLogger(SchiriRepo.class);

    Optional<SchiriConfiguration> findConfigByEmail(String emailAddress) throws SchiriRepoException;

    Optional<SchiriConfiguration> findConfigByName(Schiedsrichter schiedsrichter) throws SchiriRepoException;
    void overwriteSchiriConfiguration(SchiriConfiguration config) throws SchiriRepoException;

    class SchiriRepoException extends Exception{
        public SchiriRepoException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    default ConfiguredSchiris fetchSchiris(Collection<String> emailAddresses){
        logger.info("Fetching {} schiris", emailAddresses.size());
        ConfiguredSchiris configuredSchiris = new ConfiguredSchiris();

        for(String knownSchiri:emailAddresses){
            try {
                Optional<SchiriConfiguration> optionalSchiriConfig = findConfigByEmail(knownSchiri);
                if (optionalSchiriConfig.isPresent() && optionalSchiriConfig.get().isComplete()) {
                    logger.info("Full config found for {}", knownSchiri);
                    configuredSchiris.fullyConfiguredSchiris.put(knownSchiri, optionalSchiriConfig.get());
                } else {
                    logger.info("Partly config found for {}", knownSchiri);
                    configuredSchiris.addPartlyConfiguredSchiri(knownSchiri);
                }
            } catch (SchiriRepo.SchiriRepoException e) {
                logger.warn("Exception occurred for {}:", knownSchiri, e);
                configuredSchiris.addException(knownSchiri, e);
            }
        }

        logger.info(
                "Finished fetching schiris: {} were fully and {} partly configured.",
                configuredSchiris.fullyConfiguredSchiris.size(),
                configuredSchiris.partlyConfiguredSchiris.size()
        );

        return configuredSchiris;
    }

    class ConfiguredSchiris{
        public Map<String, SchiriConfiguration> fullyConfiguredSchiris = new HashMap<>();
        public Map<String, List<Exception>> partlyConfiguredSchiris = new HashMap<>();
        public void addPartlyConfiguredSchiri(String emailAddress){
            if(!partlyConfiguredSchiris.containsKey(emailAddress)){
                partlyConfiguredSchiris.put(emailAddress, new ArrayList<>());
            }
        }
        public void addException(String emailAddress, Exception e){
            addPartlyConfiguredSchiri(emailAddress);
            partlyConfiguredSchiris.get(emailAddress).add(e);
        }
    }
}
