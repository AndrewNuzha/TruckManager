package com.myprojects.truckmanager.truckManagerApp.service.impl;

import com.myprojects.truckmanager.truckManagerApp.model.*;
import com.myprojects.truckmanager.truckManagerApp.repository.TruckDetailsRepository;
import com.myprojects.truckmanager.truckManagerApp.repository.TruckRepository;
import com.myprojects.truckmanager.truckManagerApp.service.LocationService;
import com.myprojects.truckmanager.truckManagerApp.service.TruckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Service
public class TruckServiceImpl implements TruckService {

    @Autowired
    TruckRepository truckRepository;
    @Autowired
    TruckDetailsRepository truckDetailsRepository;
    @Autowired
    LocationService locationService;

    /**
     * Creates new starter truck with details for a new company
     *
     * @return created truck
     */
    //TODO add ready truck models
    @Override
    public Truck createStarterTruck() {
        List<Location> allLocations = locationService.getAllLocations();
        Location starterLocation = allLocations.stream().filter(loc -> loc.getCity().equals("Saint-Petersburg"))
                .findAny().orElse(allLocations.get(0));

        Truck truck = new Truck();
        truck.setModel("Scania RX100");
        truck.setCategory(TruckCategory.VAN.getCategory);
        truck.setMaxLoad(10);
        truck.setStatus(TruckStatus.AVAILABLE.getStatus);

        TruckDetails truckDetails = new TruckDetails();
        truckDetails.setMileage(0F);
        truckDetails.setMileageBeforeService(25000F);
        truckDetails.setFuelConsumption(0.5F);
        truckDetails.setCurrentLocation(starterLocation);
        truckDetails.setProductionYear(new Timestamp(new Date().getTime()));

        truck.setDetails(truckDetails);
        return truck;
    }

    @Override
    @Transactional(readOnly = true)
    public Truck findTruckWithDetailsById(Long id) {
        return truckRepository.findTruckById(id);
    }

    @Override
    @Transactional
    public void updateTruckStatus(String newStatus, Long id) {
        truckRepository.updateTruckStatus(newStatus, id);
    }

    @Override
    @Transactional
    public void updateTruckMileage(Truck truck, Float passedDistance) {
        float distanceBeforeService = 0f;
        if (truck.getDetails().getMileageBeforeService() > passedDistance) {
            distanceBeforeService = truck.getDetails().getMileageBeforeService() - passedDistance;
        } else {
            updateTruckStatus(TruckStatus.SERVICE.getStatus, truck.getId());
        }
        truckDetailsRepository.updateTruckMileage(truck.getDetails().getMileage() + passedDistance,
                distanceBeforeService, truck.getId());
    }

    @Override
    @Transactional
    public void updateTruckLocation(Location location, Long truckId) {
        truckDetailsRepository.updateTruckLocation(location, truckId);
    }

    @Override
    @Transactional
    public void saveTruck(Truck truck) {
        truckRepository.save(truck);
    }
}
