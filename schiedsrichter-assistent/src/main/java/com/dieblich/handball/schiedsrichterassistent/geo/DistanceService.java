package com.dieblich.handball.schiedsrichterassistent.geo;

import com.dieblich.handball.schiedsrichterassistent.geo.Distance;
import com.dieblich.handball.schiedsrichterassistent.geo.openroute.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

public class DistanceService {

    private GeoCodeService geoCodeService;
    private DirectionService directionService;

    public DistanceService(String apikey){
        this(new GeoCodeService(apikey), new DirectionService(apikey));
    }
    public DistanceService(GeoCodeService geoCodeService, DirectionService directionService){
        this.geoCodeService = geoCodeService;
        this.directionService = directionService;
    }

    public Optional<Distance> getTestDistance(){

        String startAddress = "Arnimstr. 108, 50825 Köln";
        String endAddress = "Am Sportzentrum, 50259 Pulheim";

        Optional<Point> start = geoCodeService.getPointForAddress(startAddress);
        if(start.isEmpty()){
            return Optional.empty();
        }

        Optional<Point> end = geoCodeService.getPointForAddress(endAddress);
        if(end.isEmpty()){
            return Optional.empty();
        }

        Optional<Directions> directions = directionService.getDirections(start.get(), end.get());

        return directions
                .flatMap(Directions::firstRoute)
                .flatMap(Route::firstSegment)
                .map(Distance::fromSegment);
    }
}
