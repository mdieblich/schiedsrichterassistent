package com.dieblich.handball.schiedsrichterassistent.config;

import com.dieblich.handball.schiedsrichterassistent.Schiedsrichter;

import java.util.Optional;

public interface SchiriRepo {

    Optional<SchiriConfiguration> findConfigByEmail(String emailAddress) throws SchiriRepoException;

    Optional<SchiriConfiguration> findConfigByName(Schiedsrichter schiedsrichter) throws SchiriRepoException;
    void overwriteSchiriConfiguration(SchiriConfiguration config) throws SchiriRepoException;

    class SchiriRepoException extends Exception{
        public SchiriRepoException(String message, Throwable cause) {
            super(message, cause);
        }
    }

}
