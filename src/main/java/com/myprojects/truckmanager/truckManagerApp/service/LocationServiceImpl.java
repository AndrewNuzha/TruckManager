package com.myprojects.truckmanager.truckManagerApp.service;

import com.myprojects.truckmanager.truckManagerApp.model.Location;
import com.myprojects.truckmanager.truckManagerApp.repository.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.apache.commons.math3.util.Precision;

import java.util.List;

@Service
public class LocationServiceImpl implements LocationService {

    final int Radius = 6371;

    @Autowired
    private LocationRepository locationRepository;

    @Override
    public Float calculateDistance(Location departure, Location arrival) {
        double latitudeOne = departure.getLatitude();
        double longitudeOne = departure.getLongitude();
        double latitudeTwo = arrival.getLatitude();
        double longitudeTwo = arrival.getLongitude();
        double latDistance = toRad(latitudeTwo-latitudeOne);
        double lonDistance = toRad(longitudeTwo-longitudeOne);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
                Math.cos(toRad(latitudeOne)) * Math.cos(toRad(latitudeTwo)) *
                        Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double distance = Radius * c;
        Float rounded = (float) Precision.round(distance, 2);
        return rounded;
    }

    @Transactional(readOnly = true)
    @Override
    public List<Location> getAllLocations() {
        return locationRepository.findAll();
    }

    private Double toRad(Double value) {
        return value * Math.PI / 180;
    }
}
