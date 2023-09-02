package com.dieblich.handball.schiedsrichterassistent.geo.openroute;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Optional;

public class DirectionService extends OpenRouteService{

    public DirectionService(String apikey){
        super(apikey);
    }

    public Optional<Directions> getDirections(Point start, Point end){
        if (start.coordinates().length < 2) {
            throw new IllegalArgumentException("Startkoordinaten müssen min. 2 Dimensionen haben: "+ start);
        } else if (end.coordinates().length < 2) {
            throw new IllegalArgumentException("Zielkoordinaten müssen min. 2 Dimensionen haben: "+ end);
        }

        HttpEntity<String> request = new HttpEntity<>("{\"coordinates\":["
                + "[" +start.coordinates()[0]+"," + start.coordinates()[1] + "],"
                + "[" +end  .coordinates()[0]+"," + end  .coordinates()[1] + "]"
                + "]}", createHeaders());

        RestTemplate restTemplate = new RestTemplate();
        Directions response = restTemplate.postForObject("https://api.openrouteservice.org/v2/directions/driving-car", request, Directions.class);

        return Optional.ofNullable(response);
    }

}
