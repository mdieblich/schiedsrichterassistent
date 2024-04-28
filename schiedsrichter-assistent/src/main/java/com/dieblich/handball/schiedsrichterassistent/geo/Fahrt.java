package com.dieblich.handball.schiedsrichterassistent.geo;

import com.dieblich.handball.schiedsrichterassistent.geo.openroute.Segment;

public record Fahrt (
   int dauerInMinuten,
   int distanzInKilometern
){
    public Fahrt(Segment segment){
        this((int)Math.ceil(segment.duration()/60), (int)Math.round(segment.distance()/1000));
    }
}
