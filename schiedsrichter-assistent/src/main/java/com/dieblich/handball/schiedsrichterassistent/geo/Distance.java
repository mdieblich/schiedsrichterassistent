package com.dieblich.handball.schiedsrichterassistent.geo;

import com.dieblich.handball.schiedsrichterassistent.geo.openroute.Segment;

public record Distance(double length, double duration) {
    public static Distance fromSegment(Segment segment){
        return new Distance(
                segment.distance(),
                segment.duration()
        );
    }
}
