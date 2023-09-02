package com.dieblich.handball.schiedsrichterassistent.geo.openroute;

public record Coordinates(double[][] coordinates) {
    public Coordinates(double start_longitude, double start_latitude, double end_longitude, double end_latitude){
        this(new double[][]{
                {start_longitude, start_latitude},
                {end_longitude, end_latitude}
        });
    }
}
