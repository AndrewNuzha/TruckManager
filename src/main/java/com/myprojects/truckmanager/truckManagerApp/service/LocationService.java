package com.myprojects.truckmanager.truckManagerApp.service;

import com.myprojects.truckmanager.truckManagerApp.model.Location;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

public interface LocationService {

    Float calculateDistance(Location departure, Location arrival);

    List<Location> getAllLocations();

    Location getLocationById(Long id);

    LocalDateTime calculateShipmentArrivalTime(Float fullDistance, Timestamp departureTime);

    LocalDateTime convertTimestampToLocalDateTime(Timestamp timestamp);

    boolean isShipmentCompleted(Float fullDistance, Timestamp departureTime);

}
