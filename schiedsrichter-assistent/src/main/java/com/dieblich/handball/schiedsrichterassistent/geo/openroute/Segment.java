package com.dieblich.handball.schiedsrichterassistent.geo.openroute;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Segment(double distance, double duration) {
    @Override
    public String toString() {
        return "Segment{" +
                "distance=" + distance +
                ", duration=" + duration +
                '}';
    }
}
