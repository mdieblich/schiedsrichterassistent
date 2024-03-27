package com.dieblich.handball.schiedsrichterassistent.geo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class DistanceController {
    @Value("${openrouteservice.apikey}")
    private String apikey;

    @GetMapping("/testroute")
    public Optional<Distance> testDistance() {

        GeoService geoService = new GeoService(apikey);
        return geoService.getTestDistance();
    }
}
