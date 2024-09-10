package com.dieblich.handball.schiedsrichterassistent.config;

@SuppressWarnings("NonAsciiCharacters")
public record Schirikosten(
        double teilnahmeEntschädigung,
        double fahrtKostenFahrer,
        double fahrtKostenBeifahrer) {

}
