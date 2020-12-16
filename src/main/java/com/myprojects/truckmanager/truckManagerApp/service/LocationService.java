package com.myprojects.truckmanager.truckManagerApp.service;

import com.myprojects.truckmanager.truckManagerApp.model.Location;

import java.sql.Timestamp;
import java.util.List;

public interface LocationService {

    Float calculateDistance(Location departure, Location arrival);

    List<Location> getAllLocations();

    Location getLocationById(Long id);

    Float calculateDistanceProgress(Float fullDistance, Timestamp departureTime);

    boolean isCompletionTime(Float fullDistance, Timestamp departureTime);

}
