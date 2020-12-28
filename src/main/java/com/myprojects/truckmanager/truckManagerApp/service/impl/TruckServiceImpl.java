package com.myprojects.truckmanager.truckManagerApp.service.impl;

import com.myprojects.truckmanager.truckManagerApp.dto.TruckDetailsDTO;
import com.myprojects.truckmanager.truckManagerApp.model.*;
import com.myprojects.truckmanager.truckManagerApp.repository.TruckDetailsRepository;
import com.myprojects.truckmanager.truckManagerApp.repository.TruckRepository;
import com.myprojects.truckmanager.truckManagerApp.service.LocationAndTimeService;
import com.myprojects.truckmanager.truckManagerApp.service.TruckService;
import org.apache.commons.math3.util.Precision;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
public class TruckServiceImpl implements TruckService {

    @Autowired
    private TruckRepository truckRepository;
    @Autowired
    private TruckDetailsRepository truckDetailsRepository;
    @Autowired
    private LocationAndTimeService locationAndTimeService;
    private List<NewTruck> newTruckList;
    private final float MILEAGE_BEFORE_SERVICE = 25000f;

    @Override
    public Truck createTruck(NewTruck newTruck) {
        List<Location> allLocations = locationAndTimeService.getAllLocations();
        Location starterLocation = allLocations.stream().filter(loc -> loc.getCity().equals("Saint-Petersburg"))
                .findAny().orElse(allLocations.get(0));

        Truck truck = new Truck();
        truck.setModel(newTruck.getModel());
        truck.setCategory(TruckCategory.VAN.getCategory);
        truck.setMaxLoad(newTruck.getMaxLoad());
        truck.setStatus(TruckStatus.AVAILABLE.getStatus);

        TruckDetails truckDetails = new TruckDetails();
        truckDetails.setMileage(0F);
        truckDetails.setMileageBeforeService(MILEAGE_BEFORE_SERVICE);
        truckDetails.setFuelConsumption(newTruck.getFuelConsumption());
        truckDetails.setCurrentLocation(starterLocation);
        truckDetails.setProductionYear(new Timestamp(new Date().getTime()));

        truckDetailsRepository.save(truckDetails);
        truck.setDetails(truckDetails);
        return truck;
    }

    @Override
    public List<NewTruck> getAllNewTrucks() {
        return newTruckList;
    }

    @Override
    public TruckDetailsDTO prepareTruckDetails(Truck truck) {
        TruckDetailsDTO truckDetailsDTO = new TruckDetailsDTO();
        truckDetailsDTO.setTruckId(truck.getId());
        truckDetailsDTO.setFuelConsumption(truck.getDetails().getFuelConsumption());
        truckDetailsDTO.setMileage(truck.getDetails().getMileage());
        truckDetailsDTO.setMileageBeforeService(truck.getDetails().getMileageBeforeService());
        truckDetailsDTO.setCurrentLocationText(truck.getDetails().getCurrentLocation().getCountry() + ", "
                + truck.getDetails().getCurrentLocation().getCity());
        return truckDetailsDTO;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean doesCompanyContainTruck(Truck truck, Long companyId) {
        Set<Truck> companyTrucks = truckRepository.findByCompany_Id(companyId);
        return (companyTrucks.contains(truck));
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
    public void sellTruck(Truck truck) {
        Company company = truck.getCompany();
        company.setBalance(company.getBalance() + 20000F);//TODO do something here with price
        truckRepository.deleteById(truck.getId());
    }

    @Override
    @Transactional
    public boolean serviceTruck(Truck truck) {
        Company company = truck.getCompany();
        if (company.getBalance() < calculateServicePrice(truck)) {
            return false;
        } else {
            company.setBalance(company.getBalance() - calculateServicePrice(truck));
            truck.getDetails().setMileageBeforeService(MILEAGE_BEFORE_SERVICE);
            return true;
        }
    }

    @Override
    @Transactional
    public void saveTruck(Truck truck) {
        truckRepository.save(truck);
    }

    private Float calculateServicePrice(Truck truck) {
        float passedDistance = MILEAGE_BEFORE_SERVICE - truck.getDetails().getMileageBeforeService();
        float price = passedDistance * 0.0065f;
        return Precision.round(price, 2);
    }

    @PostConstruct
    private void postConstruct() {
        newTruckList = new ArrayList<>();
        newTruckList.add(new NewTruck(1, "Scania RX100", 10, 0.52f, 15000));
        newTruckList.add(new NewTruck(2, "MAN 818", 20, 0.48f, 20000));
    }
}
