package com.dieblich.handball.schiedsrichterassistent.mail.received;

import com.dieblich.handball.schiedsrichterassistent.Schiedsrichter;
import com.dieblich.handball.schiedsrichterassistent.SchiriEinsatz;
import com.dieblich.handball.schiedsrichterassistent.mail.Email;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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

        List<SchiriEinsatz> einsaetze = new ArrayList<>();

        SchiriEinsatzBuilder builder = new SchiriEinsatzBuilder();

        String[] allLines = emailContent.split("\\r?\\n|\\r");
        for(int i=0; i<allLines.length; i++){
            String line = allLines[i].trim();
            line = removeIntentions(line);
            if(line.startsWith("Liga:")){
                builder.liga = line.substring("Liga:".length()).trim();
            } else if (line.startsWith("Ort:")){
                String ortsangabe = line.substring("Ort:".length());
                String[] ortsAngabenTeile = ortsangabe.split(", ");
                if(ortsAngabenTeile.length >= 3) {
                    builder.halleName = ortsAngabenTeile[0].trim();
                    builder.halleStrasse = ortsAngabenTeile[1].trim();
                    builder.hallePLZOrt = ortsAngabenTeile[2].trim();
                }
            } else if(line.startsWith("Spiel-Nr:")){
                builder.spielNr = line.substring("Spiel-Nr:".length()).trim();
                // Die Spiel-Angaben stehen in der nächsten Zeile
                if(i<allLines.length-1){
                    String nextLine = allLines[++i].trim();
                    nextLine = removeIntentions(nextLine);
                    String anwurfString = nextLine.substring(0,16);
                    builder.anwurf = LocalDateTime.parse(anwurfString, FORMATTER);
                    String begegnung = nextLine.substring(16);
                    String[] gegner = begegnung.split(" - ");
                    if(gegner.length>=2){
                        builder.heimMannschaft = gegner[0].trim();
                        builder.gastMannschaft = gegner[1].trim();
                    }
                }
            } else if(line.startsWith("SR-Gespann:")){
                String gespann = line.substring("SR-Gespann:".length()).trim();
                String[] schiris = gespann.split("/");
                if(schiris.length >= 1){
                    builder.schiriA = Schiedsrichter.fromNachnameVorname(schiris[0].trim());
                }
                if(schiris.length >= 2){
                    builder.schiriB = Schiedsrichter.fromNachnameVorname(schiris[1].trim());
                }
            }

            // now check if everything is complete, then we have a full Einsatz
            if( builder.isComplete()){
                einsaetze.add(builder.build());
                builder.reset();
            }
        }

        return einsaetze;
    }

    private static String removeIntentions(String line){
        while(line.startsWith(" ") || line.startsWith(">")){
            line = line.substring(1);
        }
        return line;
    }

    private static class SchiriEinsatzBuilder{
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

        public boolean isComplete(){
            return  spielNr        != null &&
                    anwurf         != null &&
                    halleName      != null &&
                    halleStrasse   != null &&
                    hallePLZOrt    != null &&
                    liga           != null &&
                    heimMannschaft != null &&
                    gastMannschaft != null &&
                    schiriA        != null;
            // no need to check schiriB. Either both are set or none
        }

        public SchiriEinsatz build(){
            return new SchiriEinsatz(
                    spielNr,
                    anwurf,
                    halleName, halleStrasse, hallePLZOrt,
                    liga, heimMannschaft, gastMannschaft,
                    schiriA, schiriB
            );
        }
        public void reset(){
            spielNr = null;
            anwurf = null;
            halleName = null;
            halleStrasse = null;
            hallePLZOrt = null;
            liga = null;
            heimMannschaft = null;
            gastMannschaft = null;

            schiriA = null;
            schiriB = null;
        }
    }

}
