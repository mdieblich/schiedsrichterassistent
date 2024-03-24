package com.dieblich.handball.schiedsrichterassistent;

import java.time.LocalDateTime;

public record SchiriEinsatz(
    LocalDateTime anwurf,
    String hallenAdresse,
    String liga,
    String heimMannschaft,
    String gastMannschaft){
}
