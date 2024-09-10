package com.dieblich.handball.schiedsrichterassistent.config;

public record Schirikosten(
        LigaKosten ligaKosten,
        int distanzFahrerInKm,
        int distanzBeifahrerInKm) {

    public double getTeilnahmeEntschaedigung() {
        return ligaKosten.teilnahmeEntschaedigung();
    }

    public double getFahrtKostenFahrer() {
        return ligaKosten.kilometerPauschaleFahrer() * distanzFahrerInKm;
    }

    public double getFahrtKostenBeifahrer() {
        return ligaKosten.kilometerPauschaleBeiFahrer() * distanzBeifahrerInKm;
    }
}
