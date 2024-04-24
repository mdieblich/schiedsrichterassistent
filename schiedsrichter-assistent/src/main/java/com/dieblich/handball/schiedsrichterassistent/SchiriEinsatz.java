package com.dieblich.handball.schiedsrichterassistent;

import java.time.LocalDateTime;

public record SchiriEinsatz(
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

    @Deprecated
    public String hallenAdresse(){
        return halleStrasse + ", " + hallePLZOrt;
    }

    public String spielNr(){
        throw new RuntimeException("Not implemented");
    }

    public Schiedsrichter otherSchiri(Schiedsrichter schiedsrichter) {
        if(schiedsrichter.equals(schiriB)){
            return schiriA;
        }
        return schiriB;
    }
}
