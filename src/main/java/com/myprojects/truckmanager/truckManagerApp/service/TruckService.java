package com.myprojects.truckmanager.truckManagerApp.service;

import com.myprojects.truckmanager.truckManagerApp.model.Location;
import com.myprojects.truckmanager.truckManagerApp.model.Truck;

public interface TruckService {

    void saveTruck(Truck truck);

    Truck createStarterTruck();

    Truck findTruckWithDetailsById(Long id);

    void updateTruckStatus(String newStatus, Long id);

    void updateTruckMileage(Truck truck, Float passedDistance);

    void updateTruckLocation(Location location, Long truckId);

}
