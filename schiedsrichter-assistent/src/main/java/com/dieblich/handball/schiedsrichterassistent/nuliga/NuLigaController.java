package com.dieblich.handball.schiedsrichterassistent.nuliga;

import com.dieblich.handball.schiedsrichterassistent.geo.Distance;
import com.dieblich.handball.schiedsrichterassistent.geo.DistanceService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class NuLigaController {
    @Value("${openrouteservice.apikey}")
    private String apikey;

    @GetMapping("/test")
    public String getApiKey() {
        return apikey;
    }


    @GetMapping("/nuliga")
    public Optional<Distance> testDistance() {
//        Jsoup

        DistanceService distanceService = new DistanceService(apikey);
        return distanceService.getTestDistance();
    }
}
