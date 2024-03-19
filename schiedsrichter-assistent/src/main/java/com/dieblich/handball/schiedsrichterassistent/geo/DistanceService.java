package com.dieblich.handball.schiedsrichterassistent.geo;

import com.dieblich.handball.schiedsrichterassistent.geo.openroute.*;

import java.util.Optional;

public class DistanceService {

    private final GeoCodeService geoCodeService;
    private final DirectionService directionService;

    public DistanceService(String apikey){
        this(new GeoCodeService(apikey), new DirectionService(apikey));
    }
    public DistanceService(GeoCodeService geoCodeService, DirectionService directionService){
        this.geoCodeService = geoCodeService;
        this.directionService = directionService;
    }

    public Optional<String> addressToGeoLocation(String address){
        Optional<Point> location = geoCodeService.getPointForAddress(address);
        return location.map(p -> p.coordinates()[0] + "," + p.coordinates()[1]);
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
