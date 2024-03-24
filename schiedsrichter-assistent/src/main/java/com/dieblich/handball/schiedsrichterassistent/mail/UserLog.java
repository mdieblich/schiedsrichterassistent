package com.dieblich.handball.schiedsrichterassistent.mail;

public class UserLog {
    private final StringBuilder builder = new StringBuilder();

    public void log(String s){
        builder.append(s).append("\n");
    }

    public String createOutput() {
        return builder.toString();
    }
}
