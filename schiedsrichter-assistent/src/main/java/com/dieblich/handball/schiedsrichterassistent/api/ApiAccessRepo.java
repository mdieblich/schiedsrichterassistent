package com.dieblich.handball.schiedsrichterassistent.api;

public interface ApiAccessRepo {
    String createAccessKey(String email) throws ApiAccessRepoException;
    boolean hasAccess(String email, String key) throws ApiAccessRepoException;

    class ApiAccessRepoException extends Exception{
        public ApiAccessRepoException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
