package com.myprojects.truckmanager.truckManagerApp.service;

import com.myprojects.truckmanager.truckManagerApp.model.Location;
import com.myprojects.truckmanager.truckManagerApp.model.Truck;

import java.util.List;

public interface LocationService {

    Float calculateDistance(Location departure, Location arrival);

    List<Location> getAllLocations();

    List<Location> getLocationsForTruck(Truck truck);

    Location getLocationById(Long id);

}
