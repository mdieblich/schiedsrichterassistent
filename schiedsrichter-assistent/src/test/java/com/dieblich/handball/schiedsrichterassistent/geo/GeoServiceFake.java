package com.dieblich.handball.schiedsrichterassistent.geo;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class GeoServiceFake implements GeoService{

    private final Map<String, Koordinaten> koordinaten = new HashMap<>();
    private final Map<AbstractMap.SimpleImmutableEntry<Koordinaten, Koordinaten>, Fahrt> fahrten = new HashMap<>();

    public void addFahrt(Koordinaten startCoords, Koordinaten endCoords, int dauerInMinuten, int distanzInKilometern){
        fahrten.put(pair(startCoords, endCoords), new Fahrt(dauerInMinuten, distanzInKilometern));
    }

    private AbstractMap.SimpleImmutableEntry<Koordinaten, Koordinaten> pair(Koordinaten a, Koordinaten b){
        return new AbstractMap.SimpleImmutableEntry<>(a, b);
    }

    public Koordinaten addKoordinaten(String address){
        double value = koordinaten.size();
        Koordinaten newCoords = new Koordinaten(value, value);
        koordinaten.put(address, newCoords);
        return newCoords;
    }

    @Override
    public Optional<Koordinaten> findKoordinaten(String address) {
        return Optional.ofNullable(koordinaten.get(address));
    }

    @Override
    public Optional<Fahrt> calculateFahrt(Koordinaten start, Koordinaten ende) {
        return Optional.ofNullable(fahrten.get(pair(start, ende)));
    }
}
