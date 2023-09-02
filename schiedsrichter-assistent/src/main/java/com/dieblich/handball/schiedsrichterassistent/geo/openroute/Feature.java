package com.dieblich.handball.schiedsrichterassistent.geo.openroute;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Feature(Point geometry) {
    @Override
    public String toString() {
        return "Feature{" +
                "geometry=" + geometry +
                '}';
    }
}
