package com.myprojects.truckmanager.truckManagerApp.service;

import com.myprojects.truckmanager.truckManagerApp.model.Truck;

public interface TruckService {

    void saveTruck(Truck truck);

    Truck createStarterTruck();

    Truck findTruckById(Long id);

    void updateTruckStatus(String newStatus, Long id);

    void updateTruckMileage(Long newMileage, Long id);

}
