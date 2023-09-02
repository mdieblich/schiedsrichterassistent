package com.dieblich.handball.schiedsrichterassistent.geo.openroute;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Arrays;
import java.util.Optional;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Directions(Route[] routes) {

    public Optional<Route> firstRoute(){
        if(routes.length == 0){
            return Optional.empty();
        }
        return Optional.of(routes[0]);
    }

    @Override
    public String toString() {
        return "Directions{" +
                "routes=" + Arrays.toString(routes) +
                '}';
    }
}
