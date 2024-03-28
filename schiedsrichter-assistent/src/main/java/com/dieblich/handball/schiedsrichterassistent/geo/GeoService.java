package com.dieblich.handball.schiedsrichterassistent.geo;

import java.util.Optional;

public interface GeoService {
    Optional<Koordinaten> findKoordinaten(String address);

    Optional<Fahrt> calculateFahrt(Koordinaten start, Koordinaten ende);
}
