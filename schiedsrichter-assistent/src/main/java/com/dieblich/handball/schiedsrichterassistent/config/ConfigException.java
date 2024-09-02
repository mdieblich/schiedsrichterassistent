package com.dieblich.handball.schiedsrichterassistent.config;

public class ConfigException extends Exception {
    public ConfigException(String s) {
        super(s);
    }

    public ConfigException(String message, Exception e) {
        super(message, e);
    }
}
