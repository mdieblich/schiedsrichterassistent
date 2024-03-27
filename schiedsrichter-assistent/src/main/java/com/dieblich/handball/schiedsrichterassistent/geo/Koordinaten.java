package com.dieblich.handball.schiedsrichterassistent.geo;

import com.dieblich.handball.schiedsrichterassistent.geo.openroute.Point;

public record Koordinaten (
    double breitengrad,
    double längengrad
){
    public Koordinaten(Point p){
        this(
            p.coordinates()[1],
            p.coordinates()[0]
        );
    }
}
