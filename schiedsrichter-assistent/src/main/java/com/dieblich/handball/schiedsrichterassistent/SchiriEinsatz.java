package com.dieblich.handball.schiedsrichterassistent;

import java.time.LocalDateTime;

public record SchiriEinsatz(
    String spielNr,
    LocalDateTime anwurf,
    String halleName,
    String halleStrasse,
    String hallePLZOrt,
    String ligaBezeichnungAusEmail,
    String heimMannschaft,
    String gastMannschaft,
    Schiedsrichter schiriA,
    Schiedsrichter schiriB){

    public boolean mitGespannspartner(){
        return schiriB != null;
    }

    public String hallenAdresse(){
        return halleStrasse + ", " + hallePLZOrt;
    }

    public Schiedsrichter otherSchiri(Schiedsrichter schiedsrichter) {
        if(schiedsrichter.equals(schiriB)){
            return schiriA;
        }
        return schiriB;
    }
}
