package com.myprojects.truckmanager.truckManagerApp.service;

import com.myprojects.truckmanager.truckManagerApp.model.Truck;
import com.myprojects.truckmanager.truckManagerApp.repository.TruckRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TruckServiceImpl implements TruckService {

    @Autowired
    TruckRepository truckRepository;

    @Override
    public void saveTruck(Truck truck) {
        truckRepository.save(truck);
    }
}
