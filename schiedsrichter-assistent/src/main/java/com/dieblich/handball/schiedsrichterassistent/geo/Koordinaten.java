package com.dieblich.handball.schiedsrichterassistent.geo;

import com.dieblich.handball.schiedsrichterassistent.geo.openroute.Point;

public record Koordinaten (

    /* latitude */
    double breitengrad,

    /* longitude */
    double längengrad
){
    public Koordinaten(Point p){
        this(
            p.coordinates()[1],
            p.coordinates()[0]
        );
    }

    public Point toPoint(){
        return new Point(längengrad, breitengrad);
    }
}
