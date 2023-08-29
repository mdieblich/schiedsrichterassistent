package com.dieblich.handball.schiedsrichterassistent.nuliga;

//import com.dieblich.geo.Distance;
//import com.dieblich.geo.openroute.DistanceService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class NuLigaController {
    @GetMapping("/nuliga")
    public String testDistance() {

//        Jsoup
//
//        DistanceService distanceService = new DistanceService("5b3ce3597851110001cf62487a0063fcd8d44f6990d7e8aa7cc44335");
//        return distanceService.getTestDistance();
        return "Hallo, Welt!";
    }
}
