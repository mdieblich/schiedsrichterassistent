package com.dieblich.handball.schiedsrichterassistent.mail;

import org.springframework.lang.Nullable;

import java.util.Optional;

public class ValueChange{
    private String old;
    private String now;

    public ValueChange(String old, String now){
        this.old = old;
        this.now = now;
    }
    public Optional<String> getOld(){
        return Optional.ofNullable(old);
    }
    public String getNow(){return now;}
}
