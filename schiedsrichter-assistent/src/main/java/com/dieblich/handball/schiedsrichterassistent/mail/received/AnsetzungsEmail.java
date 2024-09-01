package com.dieblich.handball.schiedsrichterassistent.mail.received;

import com.dieblich.handball.schiedsrichterassistent.Schiedsrichter;
import com.dieblich.handball.schiedsrichterassistent.SchiriEinsatz;
import com.dieblich.handball.schiedsrichterassistent.mail.Email;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AnsetzungsEmail {

    private final Email originalEmail;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    public AnsetzungsEmail(Email originalEmail){
        this.originalEmail = originalEmail;
    }

    public List<SchiriEinsatz> extractSchiriEinsaetze() {
        return extractSchiriEinsaetze(originalEmail.getContent());
    }

    static List<SchiriEinsatz> extractSchiriEinsaetze(String emailContent){
        String spielNr = null;
        LocalDateTime anwurf = null;
        String halleName = null;
        String halleStrasse = null;
        String hallePLZOrt = null;
        String liga = null;
        String heimMannschaft = null;
        String gastMannschaft = null;

        Schiedsrichter schiriA = null;
        Schiedsrichter schiriB = null;

        String[] allLines = emailContent.split("\\r?\\n|\\r");
        for(int i=0; i<allLines.length; i++){
            String line = allLines[i].trim();
            if(line.startsWith("Liga:")){
                liga = line.substring("Liga:".length()).trim();
            } else if (line.startsWith("Ort:")){
                String ortsangabe = line.substring("Ort:".length());
                String[] ortsAngabenTeile = ortsangabe.split(", ");
                if(ortsAngabenTeile.length >= 3) {
                    halleName = ortsAngabenTeile[0].trim();
                    halleStrasse = ortsAngabenTeile[1].trim();
                    hallePLZOrt = ortsAngabenTeile[2].trim();
                }
            } else if(line.startsWith("Spiel-Nr:")){
                spielNr = line.substring("Spiel-Nr:".length()).trim();
                // Die Spiel-Angaben stehen in der nächsten Zeile
                if(i<allLines.length-1){
                    String nextLine = allLines[++i].trim();
                    String anwurfString = nextLine.substring(0,16);
                    anwurf = LocalDateTime.parse(anwurfString, FORMATTER);
                    String begegnung = nextLine.substring(16);
                    String[] gegner = begegnung.split(" - ");
                    if(gegner.length>=2){
                        heimMannschaft = gegner[0].trim();
                        gastMannschaft = gegner[1].trim();
                    }
                }
            } else if(line.startsWith("SR-Gespann:")){
                String gespann = line.substring("SR-Gespann:".length()).trim();
                String[] schiris = gespann.split("/");
                if(schiris.length >= 1){
                    schiriA = Schiedsrichter.fromNachnameVorname(schiris[0].trim());
                }
                if(schiris.length >= 2){
                    schiriB = Schiedsrichter.fromNachnameVorname(schiris[1].trim());
                }
            }
        }

        return List.of(new SchiriEinsatz(
                spielNr,
                anwurf,
                halleName, halleStrasse, hallePLZOrt,
                liga, heimMannschaft, gastMannschaft,
                schiriA, schiriB
        ));
    }

}
