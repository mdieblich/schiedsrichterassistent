package com.dieblich.handball.schiedsrichterassistent;

import java.time.LocalDateTime;

public record SchiriEinsatz(
    LocalDateTime anwurf,
    String hallenAdresse,
    String ligaBezeichnungAusEmail,
    String heimMannschaft,
    String gastMannschaft,
    Schiedsrichter schiriA,
    Schiedsrichter schirirB){

}
