package com.dieblich.handball.schiedsrichterassistent.geo;

import com.dieblich.handball.schiedsrichterassistent.geo.openroute.Segment;

public record Fahrt (
   int dauerInMinuten,
   int distanzInKilometern
){
    public static final Fahrt NULL = new Fahrt(0,0);

    public Fahrt(Segment segment){
        this((int)Math.ceil(segment.duration()/60), (int)Math.round(segment.distance()/1000));
    }
}
