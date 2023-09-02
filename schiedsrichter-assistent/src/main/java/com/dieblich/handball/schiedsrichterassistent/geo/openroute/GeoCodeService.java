package com.dieblich.handball.schiedsrichterassistent.geo.openroute;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class GeoCodeService extends OpenRouteService{
    public GeoCodeService(String apikey){
        super(apikey);
    }

    public Optional<Point> getPointForAddress(String address){
        if(address.isBlank()){
            return Optional.empty();
        }

        String encodedAddress = URLEncoder.encode(address, StandardCharsets.UTF_8);
        // now a special hack: OpenRouteService somehow needs the "comma" unencoded, to correctly detect street + housenumber
        encodedAddress = encodedAddress.replace("%2C", ",");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<GeoCode> response = restTemplate.exchange("https://api.openrouteservice.org/geocode/search?api_key="+getApikey()+"&size=1&text="+encodedAddress, HttpMethod.GET, requestEntity, GeoCode.class);

        return Optional.ofNullable(response.getBody())
                .flatMap(GeoCode::firstFeature)
                .map(Feature::geometry);
    }
}

