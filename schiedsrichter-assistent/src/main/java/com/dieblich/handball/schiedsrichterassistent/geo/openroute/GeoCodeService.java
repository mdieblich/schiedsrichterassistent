package com.dieblich.handball.schiedsrichterassistent.geo.openroute;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

public class GeoCodeService extends OpenRouteService{
    public GeoCodeService(String apikey){
        super(apikey);
    }

    public Optional<Point> getPointForAddress(String address){
        if(address.isBlank()){
            return Optional.empty();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(
                MediaType.APPLICATION_JSON
        ));
        headers.setAcceptCharset(List.of(StandardCharsets.UTF_8));
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();
        String fullURL = "https://api.openrouteservice.org/geocode/search?api_key="+getApikey()+"&text="+address+"&size=1";
        ResponseEntity<GeoCode> response = restTemplate.exchange(fullURL, HttpMethod.GET, requestEntity, GeoCode.class);

        return Optional.ofNullable(response.getBody())
                .flatMap(GeoCode::firstFeature)
                .map(Feature::geometry);
    }
}

