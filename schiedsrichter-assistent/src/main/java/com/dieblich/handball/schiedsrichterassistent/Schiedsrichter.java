package com.dieblich.handball.schiedsrichterassistent;

public record Schiedsrichter(String vorname, String nachname) {

    public String fullName(){
        return vorname + " " + nachname;
    }
    public static Schiedsrichter fromNachnameVorname(String nachname_vorname) {
        int firstSpace = nachname_vorname.indexOf(' ');
        String vornamen = nachname_vorname.substring(firstSpace).trim();
        String nachname = nachname_vorname.substring(0, firstSpace).trim();
        return new Schiedsrichter(vornamen, nachname);
    }
}
