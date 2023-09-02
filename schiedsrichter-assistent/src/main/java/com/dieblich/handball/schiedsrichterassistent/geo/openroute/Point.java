package com.dieblich.handball.schiedsrichterassistent.geo.openroute;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Arrays;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Point(double[] coordinates) {
    public Point(double longitude, double latitude){
        this(new double[]{longitude, latitude});
    }

    @Override
    public String toString() {
        return "Point{" +
                "coordinates=" + Arrays.toString(coordinates) +
                '}';
    }
}
