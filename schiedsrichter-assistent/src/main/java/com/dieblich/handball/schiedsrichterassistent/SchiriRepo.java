package com.dieblich.handball.schiedsrichterassistent;

import java.util.Optional;

public interface SchiriRepo {

    ConfigurationStatus getConfigurationStatus(String emailAddress) throws SchiriRepoException;

    Optional<SchiriConfiguration> findConfigByEmail(String emailAddress) throws SchiriRepoException;

    Optional<SchiriConfiguration> findConfigByName(Schiedsrichter schiedsrichter) throws SchiriRepoException;
    void overwriteSchiriConfiguration(SchiriConfiguration config) throws SchiriRepoException;

    class SchiriRepoException extends Exception{
        public SchiriRepoException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    enum ConfigurationStatus{
        NEW,
        INCOMPLETE,
        COMPLETE
    }
}
