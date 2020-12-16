package com.myprojects.truckmanager.truckManagerApp.service.impl;

import com.myprojects.truckmanager.truckManagerApp.model.Location;
import com.myprojects.truckmanager.truckManagerApp.repository.LocationRepository;
import com.myprojects.truckmanager.truckManagerApp.service.LocationService;
import org.apache.commons.math3.util.Precision;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.List;

@Service
public class LocationServiceImpl implements LocationService {

    private final int RADIUS = 6371;
    private final int SPEED = 90;
    private final int SPEED_COEFFICIENT = 13;

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
    public Float calculateDistanceProgress(Float fullDistance, Timestamp departureTime) {
        LocalDateTime departureConvertedTime = resolveCurrentTimeZone(departureTime);
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(departureConvertedTime, now);
        float hoursDuration = (float) duration.getSeconds() / 3600;
        float passedKilometers = hoursDuration * SPEED * SPEED_COEFFICIENT;
        float passedDistancePercentage = passedKilometers * 100 / fullDistance;
        return Precision.round(passedDistancePercentage, 2);
    }

    @Override
    public boolean isCompletionTime(Float fullDistance, Timestamp departureTime) {
        LocalDateTime departureConvertedTime = resolveCurrentTimeZone(departureTime);
        float shipmentTimeInSeconds = (fullDistance / (SPEED * SPEED_COEFFICIENT)) * 3600;
        LocalDateTime shipmentCompletingTime = departureConvertedTime.plusSeconds((long) shipmentTimeInSeconds);
        LocalDateTime now = LocalDateTime.now();

        return shipmentCompletingTime.isBefore(now);
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

    private Double toRad(Double value) {
        return value * Math.PI / 180;
    }

    private LocalDateTime resolveCurrentTimeZone(Timestamp timestamp) {
        ZoneId zoneId = Calendar.getInstance().getTimeZone().toZoneId();
        return timestamp.toInstant().atZone(zoneId).toLocalDateTime();
    }
}
