package com.dieblich.handball.schiedsrichterassistent.geo;

import com.dieblich.handball.schiedsrichterassistent.geo.openroute.Segment;

public record Fahrt (
   int dauerInSekunden,
   int distanzInMetern
){
    public Fahrt(Segment segment){
        this((int)segment.duration(), (int)segment.distance());
    }
}
