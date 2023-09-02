package com.dieblich.handball.schiedsrichterassistent.geo.openroute;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Arrays;
import java.util.Optional;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GeoCode(Feature[] features) {

    public Optional<Feature> firstFeature(){
        if(features.length == 0){
            return Optional.empty();
        }
        return Optional.of(features[0]);
    }

    @Override
    public String toString() {
        return "GeoCode{" +
                "features=" + Arrays.toString(features) +
                '}';
    }
}
