package com.dieblich.handball.schiedsrichterassistent.geo;

import com.dieblich.handball.schiedsrichterassistent.geo.openroute.*;

import java.util.Optional;

public class GeoService {

    private final GeoCodeService geoCodeService;
    private final DirectionService directionService;

    public GeoService(String apikey){
        this(new GeoCodeService(apikey), new DirectionService(apikey));
    }
    public GeoService(GeoCodeService geoCodeService, DirectionService directionService){
        this.geoCodeService = geoCodeService;
        this.directionService = directionService;
    }


    public Optional<Koordinaten> findKoordinaten(String address){
        return geoCodeService.getPointForAddress(address).map(Koordinaten::new);
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
