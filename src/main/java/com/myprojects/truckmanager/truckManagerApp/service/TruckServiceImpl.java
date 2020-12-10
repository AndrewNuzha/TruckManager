package com.myprojects.truckmanager.truckManagerApp.service;

import com.myprojects.truckmanager.truckManagerApp.model.*;
import com.myprojects.truckmanager.truckManagerApp.repository.TruckDetailsRepository;
import com.myprojects.truckmanager.truckManagerApp.repository.TruckRepository;
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
     * @return created truck
     */
    @Override
    public Truck createStarterTruck() {
        List<Location> allLocations = locationService.getAllLocations();
        Location starterLocation = allLocations.stream().filter(loc -> loc.getCity().equals("Saint-Petersburg"))
                .findAny().orElse(allLocations.get(0));

        Truck truck = new Truck();
        truck.setModel("Scania RX100");
        truck.setFuelConsumption(0.41f);
        truck.setMileage(0L);
        truck.setCategory(TruckCategory.VAN.getCategory);
        truck.setStatus(TruckStatus.AVAILABLE.getStatus);

        TruckDetails truckDetails = new TruckDetails();
        truckDetails.setMileageBeforeService(25000L);
        truckDetails.setCurrentLocation(starterLocation);
        truckDetails.setProductionYear(new Timestamp(new Date().getTime()));

        truck.setDetails(truckDetails);
        return truck;
    }

    @Override
    @Transactional(readOnly = true)
    public Truck findTruckById(Long id) {
        return truckRepository.findTruckById(id);
    }

    @Override
    @Transactional
    public void saveTruck(Truck truck) {
        truckRepository.save(truck);
    }
}
