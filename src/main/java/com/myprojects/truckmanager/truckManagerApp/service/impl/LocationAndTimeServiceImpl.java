package com.myprojects.truckmanager.truckManagerApp.service.impl;

import com.myprojects.truckmanager.truckManagerApp.model.Location;
import com.myprojects.truckmanager.truckManagerApp.repository.LocationRepository;
import com.myprojects.truckmanager.truckManagerApp.service.LocationAndTimeService;
import org.apache.commons.math3.util.Precision;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Service
public class LocationAndTimeServiceImpl implements LocationAndTimeService {

    private final int RADIUS = 6371;
    private final int SPEED = 90;
    private final int SPEED_COEFFICIENT = 180;

    @Autowired
    private LocationRepository locationRepository;

    @Override
    public Float calculateDistance(Location departure, Location arrival) {
        double latitudeOne = departure.getLatitude();
        double longitudeOne = departure.getLongitude();
        double latitudeTwo = arrival.getLatitude();
        double longitudeTwo = arrival.getLongitude();
        double latDistance = toRad(latitudeTwo - latitudeOne);
        double lonDistance = toRad(longitudeTwo - longitudeOne);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
                Math.cos(toRad(latitudeOne)) * Math.cos(toRad(latitudeTwo)) *
                        Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = RADIUS * c;
        return (float) Precision.round(distance, 2);
    }

    @Override
    public boolean isShipmentCompleted(Float fullDistance, Timestamp departureTime) {
        LocalDateTime shipmentCompletingTime = calculateShipmentArrivalTime(fullDistance, departureTime);
        LocalDateTime now = LocalDateTime.now();
        return shipmentCompletingTime.isBefore(now);
    }

    @Override
    public LocalDateTime calculateShipmentArrivalTime(Float fullDistance, Timestamp departureTime) {
        LocalDateTime departureConvertedTime = resolveCurrentTimeZone(departureTime);
        float shipmentTimeInSeconds = (fullDistance / (SPEED * SPEED_COEFFICIENT)) * 3600;
        return departureConvertedTime.plusSeconds((long) shipmentTimeInSeconds);
    }

    @Override
    public LocalDateTime convertTimestampToLocalDateTime(Timestamp timestamp) {
        return resolveCurrentTimeZone(timestamp);
    }

    @Override
    @Transactional(readOnly = true)
    public Location getLocationById(Long id) {
        return locationRepository.findLocationById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Location> getAllLocations() {
        return locationRepository.findAll();
    }

    @Override
    @Transactional
    public void initializeLocationList() {
        Location locationOne = new Location("Russia", "Saint-Petersburg", 59.939095, 30.315868);
        Location locationTwo = new Location("Russia", "Moscow", 55.755814, 37.617635);
        Location locationThree = new Location("Russia", "Kazan", 55.796127, 49.106405);
        List<Location> locationList = List.of(locationOne, locationTwo, locationThree);

        locationRepository.saveAll(locationList);
    }

    private Double toRad(Double value) {
        return value * Math.PI / 180;
    }

    private LocalDateTime resolveCurrentTimeZone(Timestamp timestamp) {
        ZoneId zoneId = Calendar.getInstance().getTimeZone().toZoneId();
        return timestamp.toInstant().atZone(zoneId).toLocalDateTime();
    }
}
