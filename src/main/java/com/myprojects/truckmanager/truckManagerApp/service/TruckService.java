package com.myprojects.truckmanager.truckManagerApp.service;

import com.myprojects.truckmanager.truckManagerApp.dto.TruckDetailsDTO;
import com.myprojects.truckmanager.truckManagerApp.model.Location;
import com.myprojects.truckmanager.truckManagerApp.model.NewTruck;
import com.myprojects.truckmanager.truckManagerApp.model.Truck;

import java.util.List;

public interface TruckService {

    void saveTruck(Truck truck);

    Truck createTruck(NewTruck newTruck);

    Truck findTruckWithDetailsById(Long id);

    TruckDetailsDTO prepareTruckDetails(Truck truck);

    List<NewTruck> getAllNewTrucks();

    void updateTruckStatus(String newStatus, Long id);

    void updateTruckMileage(Truck truck, Float passedDistance);

    void updateTruckLocation(Location location, Long truckId);

    void sellTruck(Truck truck);

    boolean serviceTruck(Truck truck);

    boolean doesCompanyContainTruck(Truck truck, Long companyId);

}
