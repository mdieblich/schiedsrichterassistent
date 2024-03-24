package com.dieblich.handball.schiedsrichterassistent.mail.received;

import com.dieblich.handball.schiedsrichterassistent.SchiriEinsatz;
import com.dieblich.handball.schiedsrichterassistent.mail.Email;
import jakarta.mail.MessagingException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AnsetzungsEmail {

    private final Email originalEmail;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    public AnsetzungsEmail(Email originalEmail){
        this.originalEmail = originalEmail;
    }

    public SchiriEinsatz extractSchiriEinsatz() throws MessagingException, IOException {
        return extractSchiriEinsatz(originalEmail.getContent());
    }

    static SchiriEinsatz extractSchiriEinsatz(String emailContent){
        LocalDateTime anwurf = null;
        String hallenAdresse = null;
        String liga = null;
        String heimMannschaft = null;
        String gastMannschaft = null;

        String[] allLines = emailContent.split("\\r?\\n|\\r");
        for(int i=0; i<allLines.length; i++){
            String line = allLines[i].trim();
            if(line.startsWith("Liga:")){
                liga = line.substring("Liga:".length()).trim();
            } else if (line.startsWith("Ort:")){
                String ortsangabe = line.substring("Ort:".length());
                String[] ortsAngabenTeile = ortsangabe.split(", ");
                if(ortsAngabenTeile.length >= 3) {
                    hallenAdresse = ortsAngabenTeile[1] + ", " + ortsAngabenTeile[2];
                }
            } else if(line.startsWith("Spiel-Nr")){
                // Die Spiel-Angaben stehen in der nächsten Zeile
                if(i<allLines.length-1){
                    String nextLine = allLines[++i].trim();
                    String anwurfString = nextLine.substring(0,16);
                    anwurf = LocalDateTime.parse(anwurfString, FORMATTER);
                    String begegnung = nextLine.substring(16);
                    String[] gegner = begegnung.split("-");
                    if(gegner.length==2){
                        heimMannschaft = gegner[0].trim();
                        gastMannschaft = gegner[1].trim();
                    }
                }
            }
        }

        return new SchiriEinsatz(anwurf, hallenAdresse, liga, heimMannschaft, gastMannschaft);
    }

}
