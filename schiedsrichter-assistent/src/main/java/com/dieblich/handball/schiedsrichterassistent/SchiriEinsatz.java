package com.dieblich.handball.schiedsrichterassistent;

import java.time.LocalDateTime;

public record SchiriEinsatz(
    LocalDateTime anwurf,
    String hallenAdresse,
    String ligaBezeichnungAusEmail,
    String heimMannschaft,
    String gastMannschaft,
    Schiedsrichter schiriA,
    Schiedsrichter schiriB){

    public boolean mitGespannspartner(){
        return schiriB != null;
    }

    public Schiedsrichter otherSchiri(Schiedsrichter schiedsrichter) {
        if(schiedsrichter.equals(schiriB)){
            return schiriA;
        }
        return schiriB;
    }
}
