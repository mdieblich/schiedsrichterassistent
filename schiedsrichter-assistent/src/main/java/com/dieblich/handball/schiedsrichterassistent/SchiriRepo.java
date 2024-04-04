package com.dieblich.handball.schiedsrichterassistent;

import java.util.Optional;

public interface SchiriRepo {
    void overwriteSchiriConfiguration(SchiriConfiguration config) throws SchiriRepoException;

    Optional<SchiriConfiguration> findConfigByEmail(String emailAddress) throws SchiriRepoException;

    Optional<SchiriConfiguration> findConfigByName(Schiedsrichter schiedsrichter) throws SchiriRepoException;

    class SchiriRepoException extends Exception{
        public SchiriRepoException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
