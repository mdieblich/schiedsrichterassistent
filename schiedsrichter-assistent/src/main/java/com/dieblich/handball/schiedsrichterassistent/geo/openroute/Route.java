package com.dieblich.handball.schiedsrichterassistent.geo.openroute;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Arrays;
import java.util.Optional;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Route(Segment[] segments) {

    public Optional<Segment> firstSegment(){
        if(segments.length == 0){
            return Optional.empty();
        }
        return Optional.of(segments[0]);
    }
    @Override
    public String toString() {
        return "Route{" +
                "segments=" + Arrays.toString(segments) +
                '}';
    }
}
