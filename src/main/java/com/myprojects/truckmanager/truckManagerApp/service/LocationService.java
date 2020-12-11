package com.myprojects.truckmanager.truckManagerApp.service;

import com.myprojects.truckmanager.truckManagerApp.model.Location;

import java.util.List;

public interface LocationService {

    Float calculateDistance(Location departure, Location arrival);

    List<Location> getAllLocations();

    Location getLocationById(Long id);

}
