package com.dieblich.handball.schiedsrichterassistent.geo.openroute;

import org.springframework.http.HttpHeaders;

public abstract class OpenRouteService {

    private final String apikey;

    public OpenRouteService(String apikey){
        this.apikey = apikey;
    }

    protected String getApikey(){
        return apikey;
    }

    protected HttpHeaders createHeaders(){
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", apikey);
        headers.add("Accept", "application/json, application/geo+json, application/gpx+xml, img/png; charset=utf-8");
        headers.add("Content-Type", "application/json; charset=utf-8");
        return headers;
    }
}
